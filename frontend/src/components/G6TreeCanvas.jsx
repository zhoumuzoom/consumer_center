import { useEffect, useRef } from 'react'
import { Empty } from 'antd'
import { Graph, treeToGraphData } from '@antv/g6'

const VIRTUAL_ROOT_ID = '__g6_virtual_root__'

function flattenTree(nodes, result = new Map()) {
  nodes.forEach((node) => {
    result.set(String(node.id), node)
    if (Array.isArray(node.children)) flattenTree(node.children, result)
  })
  return result
}

function toGraphTree(treeData) {
  const roots = Array.isArray(treeData) ? treeData : treeData ? [treeData] : []
  if (!roots.length) return null
  if (roots.length === 1) return roots[0]
  return {
    id: VIRTUAL_ROOT_ID,
    nodeName: '',
    __virtual: true,
    children: roots
  }
}

function createGraphData(treeData) {
  const root = toGraphTree(treeData)
  if (!root) return { nodes: [], edges: [] }
  return treeToGraphData(root, {
    getNodeData: (node, depth) => {
      const { children, ...rest } = node
      return {
        ...rest,
        id: String(node.id),
        depth,
        children: children?.map((child) => String(child.id))
      }
    },
    getEdgeData: (source, target) => ({
      source: String(source.id),
      target: String(target.id)
    })
  })
}

export default function G6TreeCanvas({
  treeData = [],
  selectedId,
  onNodeClick,
  getNodeLabel = (node) => node.nodeName,
  getNodeCaption,
  emptyDescription = '暂无数据'
}) {
  const containerRef = useRef(null)
  const graphRef = useRef(null)
  const nodeMapRef = useRef(new Map())
  const onNodeClickRef = useRef(onNodeClick)
  const selectedIdRef = useRef(selectedId)

  onNodeClickRef.current = onNodeClick
  selectedIdRef.current = selectedId

  useEffect(() => {
    const root = toGraphTree(treeData)
    if (!containerRef.current || !root) return undefined

    nodeMapRef.current = flattenTree(Array.isArray(treeData) ? treeData : [treeData])
    const graph = new Graph({
      container: containerRef.current,
      autoResize: true,
      autoFit: 'view',
      padding: [28, 36, 28, 36],
      data: createGraphData(treeData),
      animation: false,
      layout: {
        type: 'compact-box',
        direction: 'LR',
        getWidth: () => 184,
        getHeight: () => 56,
        getVGap: () => 18,
        getHGap: () => 64
      },
      node: {
        type: 'rect',
        style: (datum) => {
          const virtual = datum.__virtual
          const selected = selectedIdRef.current != null && String(selectedIdRef.current) === String(datum.id)
          const level = Number(datum.level ?? datum.depth ?? 0)
          const rootNode = Number(datum.depth) === 0 || level === 0
          const label = virtual ? '' : getNodeLabel(datum)
          const caption = virtual || !getNodeCaption ? '' : getNodeCaption(datum)
          return {
            size: [184, 56],
            radius: 0,
            fill: virtual ? 'transparent' : selected || rootNode ? '#002FA7' : '#FFFFFF',
            fillOpacity: virtual ? 0 : 1,
            stroke: virtual ? 'transparent' : selected || rootNode ? '#002FA7' : '#A7B1C2',
            lineWidth: virtual ? 0 : selected ? 2 : 1,
            labelText: caption ? `${label}\n${caption}` : label,
            labelFill: virtual ? 'transparent' : selected || rootNode ? '#FFFFFF' : '#172033',
            labelFontSize: 13,
            labelLineHeight: 20,
            labelPlacement: 'center',
            labelTextAlign: 'center',
            labelTextBaseline: 'middle',
            labelOpacity: virtual ? 0 : 1,
            cursor: virtual ? 'default' : 'pointer'
          }
        },
        state: {
          selected: {
            fill: '#002FA7',
            stroke: '#002FA7',
            lineWidth: 2,
            labelFill: '#FFFFFF'
          },
          active: {
            stroke: '#002FA7',
            lineWidth: 2
          }
        }
      },
      edge: {
        type: 'cubic-horizontal',
        style: (datum) => ({
          stroke: datum.source === VIRTUAL_ROOT_ID ? 'transparent' : '#C7CEDA',
          lineWidth: 1,
          endArrow: false
        })
      },
      behaviors: [
        'drag-canvas',
        'zoom-canvas',
        'hover-activate',
        { type: 'collapse-expand', trigger: 'dblclick', animation: false }
      ]
    })

    const handleNodeClick = (event) => {
      const nodeId = event?.target?.id
      if (!nodeId || nodeId === VIRTUAL_ROOT_ID) return
      const node = nodeMapRef.current.get(String(nodeId))
      if (node) onNodeClickRef.current?.(node)
    }

    graph.on('node:click', handleNodeClick)
    graphRef.current = graph
    void graph.render().then(() => {
      if (selectedIdRef.current != null && nodeMapRef.current.has(String(selectedIdRef.current))) {
        void graph.setElementState(String(selectedIdRef.current), 'selected')
      }
    })

    return () => {
      graph.off('node:click', handleNodeClick)
      graph.destroy()
      graphRef.current = null
    }
  }, [treeData, getNodeCaption, getNodeLabel])

  useEffect(() => {
    const graph = graphRef.current
    if (!graph) return
    const selectedNodes = graph.getElementDataByState('node', 'selected')
    selectedNodes.forEach((node) => {
      if (String(node.id) !== String(selectedId ?? '')) void graph.setElementState(node.id, [])
    })
    if (selectedId != null && nodeMapRef.current.has(String(selectedId))) {
      void graph.setElementState(String(selectedId), 'selected')
    }
  }, [selectedId])

  const hasData = Array.isArray(treeData) ? treeData.length > 0 : Boolean(treeData)
  return (
    <div className="g6-tree-canvas-shell">
      <div ref={containerRef} className="g6-tree-canvas" />
      {!hasData && <div className="g6-tree-empty"><Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={emptyDescription} /></div>}
    </div>
  )
}
