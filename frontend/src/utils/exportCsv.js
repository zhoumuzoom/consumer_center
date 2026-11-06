const escapeCell = (value) => `"${String(value ?? '').replace(/"/g, '""')}"`

/**
 * 纯前端导出 CSV 文件，带 UTF-8 BOM 以便 Excel 正确识别中文
 * @param {string} filename 下载文件名
 * @param {string[]} headers 表头
 * @param {Array<Array<string|number>>} rows 数据行
 */
export function downloadCsv(filename, headers, rows) {
  const content = [headers, ...rows].map((row) => row.map(escapeCell).join(',')).join('\r\n')
  const blob = new Blob([`\uFEFF${content}`], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}
