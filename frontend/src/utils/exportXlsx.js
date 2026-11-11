const COLUMN_WIDTH = 18

/**
 * 纯前端生成并下载 XLSX 文件，不经过后端
 * @param {object} params
 * @param {string} params.filename 下载文件名
 * @param {string} params.sheetName 工作表名称
 * @param {string[]} params.headers 表头
 * @param {Array<Array<string|number>>} params.rows 数据行
 */
export async function downloadXlsx({ filename, sheetName, headers, rows }) {
  // 按需加载生成库，避免进入客户列表首屏包
  const { default: writeXlsxFile } = await import('write-excel-file/browser')
  const sheetData = [
    headers.map((value) => ({ value, fontWeight: 'bold', backgroundColor: '#F2F3F5' })),
    ...rows.map((row) => row.map((value) => ({ type: String, value: value == null ? '' : String(value) })))
  ]
  await writeXlsxFile(sheetData, {
    sheet: sheetName,
    columns: headers.map(() => ({ width: COLUMN_WIDTH })),
    stickyRowsCount: 1
  }).toFile(filename)
}
