export function downloadBlob(blob, filename) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.style.display = 'none'
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  setTimeout(() => {
    URL.revokeObjectURL(url)
    document.body.removeChild(link)
  }, 100)
}
