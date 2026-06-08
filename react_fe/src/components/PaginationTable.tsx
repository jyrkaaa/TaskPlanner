import { ReactNode } from 'react'

export interface Column<T> {
  header: string
  render: (row: T) => ReactNode
}

interface PaginationTableProps<T> {
  columns: Column<T>[]
  data: T[]
  totalElements: number
  totalPages: number
  page: number
  size: number
  loading?: boolean
  onPageChange: (page: number) => void
  onSizeChange: (size: number) => void
  onRowClick?: (row: T) => void
}

const PAGE_SIZES = [10, 50, 100]

function PaginationTable<T>({
  columns,
  data,
  totalElements,
  totalPages,
  page,
  size,
  loading = false,
  onPageChange,
  onSizeChange,
  onRowClick,
}: PaginationTableProps<T>) {
  const from = totalElements === 0 ? 0 : page * size + 1
  const to = Math.min((page + 1) * size, totalElements)
  const safePages = Math.max(1, totalPages)

  return (
    <div>
      <div style={{ overflowX: 'auto' }}>
        <table className="data-table">
          <thead>
            <tr>
              {columns.map((col, i) => (
                <th key={i}>{col.header}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={columns.length} className="empty-state">
                  Loading…
                </td>
              </tr>
            ) : data.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="empty-state">
                  No results
                </td>
              </tr>
            ) : (
              data.map((row, i) => (
                <tr
                  key={i}
                  className={onRowClick ? 'data-table-row--clickable' : undefined}
                  onClick={() => onRowClick?.(row)}
                >
                  {columns.map((col, j) => (
                    <td key={j}>{col.render(row)}</td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div className="pagination-controls">
        <span className="pagination-info">
          {totalElements === 0 ? '0 results' : `${from}–${to} of ${totalElements}`}
        </span>
        <div className="pagination-nav">
          <select
            className="size-select"
            value={size}
            onChange={e => {
              onSizeChange(Number(e.target.value))
              onPageChange(0)
            }}
          >
            {PAGE_SIZES.map(s => (
              <option key={s} value={s}>
                {s} / page
              </option>
            ))}
          </select>
          <button
            className="btn btn--secondary"
            disabled={page === 0}
            onClick={() => onPageChange(page - 1)}
          >
            ←
          </button>
          <span className="pagination-page">
            Page {page + 1} of {safePages}
          </span>
          <button
            className="btn btn--secondary"
            disabled={page + 1 >= totalPages}
            onClick={() => onPageChange(page + 1)}
          >
            →
          </button>
        </div>
      </div>
    </div>
  )
}

export default PaginationTable
