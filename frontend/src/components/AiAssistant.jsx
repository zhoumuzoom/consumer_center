import { RobotOutlined, SendOutlined, StopOutlined } from '@ant-design/icons'
import { App, Button, Drawer, Input, Spin } from 'antd'
import { useEffect, useRef, useState } from 'react'
import { clearAIConversation, getAIHistory, streamAIChat } from '../api/ai'
import { useLocale } from '../locales'

const CONVERSATION_KEY = 'cmi_ai_conversation_id'

export default function AiAssistant() {
  const { t } = useLocale()
  const { message } = App.useApp()
  const [open, setOpen] = useState(false)
  const [messages, setMessages] = useState([])
  const [input, setInput] = useState('')
  const [streaming, setStreaming] = useState(false)
  const [loadingHistory, setLoadingHistory] = useState(false)
  const abortRef = useRef(null)
  const bottomRef = useRef(null)
  const loadedConversationRef = useRef('')
  const conversationIdRef = useRef(localStorage.getItem(CONVERSATION_KEY) || '')

  useEffect(() => {
    if (!open) return
    const behavior = messages.some((item) => item.streaming) ? 'auto' : 'smooth'
    bottomRef.current?.scrollIntoView({ behavior, block: 'end' })
  }, [messages, open])

  useEffect(() => {
    if (!open) return
    const conversationId = conversationIdRef.current
    if (!conversationId || loadedConversationRef.current === conversationId) return
    let cancelled = false
    setLoadingHistory(true)
    getAIHistory(conversationId)
      .then((res) => {
        if (cancelled) return
        const history = (res.data || []).filter((item) => item.content)
        setMessages(history.map((item, index) => ({ id: `history-${index}`, role: item.role, content: item.content })))
        loadedConversationRef.current = conversationId
      })
      .catch(() => {})
      .finally(() => { if (!cancelled) setLoadingHistory(false) })
    return () => { cancelled = true }
  }, [open])

  const send = async (rawText) => {
    const content = (rawText ?? input).trim()
    if (!content || streaming || loadingHistory) return
    setInput('')
    const userMessage = { id: `user-${Date.now()}`, role: 'user', content }
    const assistantId = `assistant-${Date.now()}`
    setMessages((prev) => [...prev, userMessage, { id: assistantId, role: 'assistant', content: '', streaming: true }])
    setStreaming(true)
    const controller = new AbortController()
    abortRef.current = controller
    try {
      await streamAIChat({
        conversationId: conversationIdRef.current || undefined,
        message: content,
        signal: controller.signal,
        onDelta: (delta) => {
          setMessages((prev) => prev.map((item) => item.id === assistantId
            ? { ...item, content: item.content + delta }
            : item))
        },
        onDone: (payload) => {
          if (payload?.conversationId) {
            conversationIdRef.current = payload.conversationId
            loadedConversationRef.current = payload.conversationId
            localStorage.setItem(CONVERSATION_KEY, payload.conversationId)
          }
        },
        onError: (error) => {
          message.error(error.message)
          setMessages((prev) => prev.map((item) => item.id === assistantId ? { ...item, error: true } : item))
        }
      })
    } catch (error) {
      if (error.name !== 'AbortError') {
        message.error(error.message || t('aiAssistant.failed'))
        setMessages((prev) => prev.map((item) => item.id === assistantId ? { ...item, error: true } : item))
      }
    } finally {
      setMessages((prev) => prev.map((item) => item.id === assistantId ? { ...item, streaming: false } : item))
      setStreaming(false)
      abortRef.current = null
    }
  }

  const stop = () => {
    abortRef.current?.abort()
  }

  const clear = async () => {
    abortRef.current?.abort()
    const conversationId = conversationIdRef.current
    if (conversationId) {
      try {
        await clearAIConversation(conversationId)
      } catch {
        // 清空失败时仍重置本地会话，避免继续使用旧上下文
      }
    }
    conversationIdRef.current = ''
    loadedConversationRef.current = ''
    localStorage.removeItem(CONVERSATION_KEY)
    setMessages([])
  }

  const onPressEnter = (event) => {
    if (event.shiftKey || event.nativeEvent?.isComposing) return
    event.preventDefault()
    send()
  }

  const suggestions = [
    t('aiAssistant.suggestion1'),
    t('aiAssistant.suggestion2'),
    t('aiAssistant.suggestion3')
  ]

  const hasMessages = messages.length > 0

  return (
    <>
      <Button
        type="primary"
        shape="circle"
        size="large"
        icon={<RobotOutlined />}
        className="ai-assistant-fab"
        aria-label={t('aiAssistant.open')}
        onClick={() => setOpen(true)}
      />
      <Drawer
        open={open}
        onClose={() => setOpen(false)}
        size="min(420px, 100vw)"
        className="ai-assistant-drawer"
        styles={{ body: { padding: 0 }, header: { padding: '14px 20px' } }}
        title={(
          <div className="ai-assistant-title">
            <span className="ai-assistant-title-icon"><RobotOutlined /></span>
            <div>
              <div className="ai-assistant-title-text">{t('aiAssistant.title')}</div>
              <div className="ai-assistant-subtitle">{t('aiAssistant.subtitle')}</div>
            </div>
          </div>
        )}
        extra={<Button type="text" onClick={clear}>{t('aiAssistant.clear')}</Button>}
      >
        <div className="ai-assistant-body">
          <div className="ai-assistant-messages" role="log" aria-live="polite" aria-label={t('aiAssistant.title')}>
            {loadingHistory && <div className="ai-assistant-loading"><Spin size="small" /> {t('aiAssistant.loading')}</div>}
            {!hasMessages && !loadingHistory && (
              <div className="ai-assistant-empty">
                <div className="ai-assistant-empty-title">{t('aiAssistant.emptyTitle')}</div>
                <div className="ai-assistant-empty-desc">{t('aiAssistant.emptyDesc')}</div>
                <div className="ai-assistant-suggestions">
                  {suggestions.map((item) => (
                    <Button
                      key={item}
                      className="ai-assistant-suggestion"
                      disabled={streaming}
                      onClick={() => send(item)}
                    >
                      {item}
                    </Button>
                  ))}
                </div>
              </div>
            )}
            {messages.map((item) => (
              <div key={item.id} className={`ai-assistant-bubble-row ${item.role}`}>
                <div className={`ai-assistant-bubble ${item.role} ${item.error ? 'error' : ''}`}>
                  {item.content || (item.streaming && <span className="ai-assistant-thinking"><Spin size="small" /> {t('aiAssistant.thinking')}</span>)}
                  {item.streaming && item.content && <span className="ai-assistant-cursor" aria-hidden="true" />}
                  {item.error && <div className="ai-assistant-error-text">{t('aiAssistant.interrupted')}</div>}
                </div>
              </div>
            ))}
            <div ref={bottomRef} />
          </div>
          <div className="ai-assistant-footer">
            <Input.TextArea
              value={input}
              onChange={(event) => setInput(event.target.value)}
              onPressEnter={onPressEnter}
              placeholder={t('aiAssistant.placeholder')}
              autoSize={{ minRows: 2, maxRows: 4 }}
              maxLength={2000}
              disabled={streaming}
            />
            <div className="ai-assistant-actions">
              <span className="ai-assistant-hint">{t('aiAssistant.hint')}</span>
              {streaming ? (
                <Button icon={<StopOutlined />} onClick={stop}>{t('aiAssistant.stop')}</Button>
              ) : (
                <Button type="primary" icon={<SendOutlined />} onClick={() => send()} disabled={!input.trim() || loadingHistory}>
                  {t('aiAssistant.send')}
                </Button>
              )}
            </div>
          </div>
        </div>
      </Drawer>
    </>
  )
}