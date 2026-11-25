import request from '../utils/request'
import { getAccessToken } from '../utils/token'

export const getAIHistory = (conversationId) => request.get(`/ai/conversations/${conversationId}/messages`)

export const clearAIConversation = (conversationId) => request.delete(`/ai/conversations/${conversationId}`)

function parseEventBlock(block) {
  let event = 'message'
  const dataLines = []
  for (const line of block.split('\n')) {
    if (line.startsWith('event:')) event = line.slice(6).trim()
    else if (line.startsWith('data:')) dataLines.push(line.slice(5).replace(/^ /, ''))
  }
  return { event, data: dataLines.join('\n') }
}

export async function streamAIChat({ conversationId, message, signal, onDelta, onDone, onError }) {
  const response = await fetch('/api/ai/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', token: getAccessToken() },
    body: JSON.stringify({ conversationId, message }),
    signal
  })
  const contentType = response.headers.get('content-type') || ''
  if (!response.ok || !contentType.includes('text/event-stream')) {
    let errorMessage = 'AI助手响应失败，请稍后重试'
    try {
      const body = await response.json()
      errorMessage = body?.message || errorMessage
    } catch {
      // 非JSON错误响应时保留默认提示
    }
    throw new Error(errorMessage)
  }
  if (!response.body) {
    throw new Error('当前浏览器不支持流式响应')
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''
  while (true) {
    const { done, value } = await reader.read()
    if (done) break
    buffer += decoder.decode(value, { stream: true }).replace(/\r\n/g, '\n')
    let boundary = buffer.indexOf('\n\n')
    while (boundary >= 0) {
      const block = buffer.slice(0, boundary).trim()
      buffer = buffer.slice(boundary + 2)
      if (block) {
        const { event, data } = parseEventBlock(block)
        const payload = data ? JSON.parse(data) : {}
        if (event === 'delta') onDelta(payload.content || '')
        else if (event === 'done') onDone(payload)
        else if (event === 'error') onError(new Error(payload.message || 'AI助手响应失败'))
      }
      boundary = buffer.indexOf('\n\n')
    }
  }
}