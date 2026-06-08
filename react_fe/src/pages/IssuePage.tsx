import { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { issuesApi } from '../api/issues'
import { IssueDto } from '../models/dto/IssueDto'
import { PaginationBaseResponse } from '../models/PaginationBaseResponse'
import PaginationTable, { Column } from '../components/PaginationTable'

const COLUMNS: Column<IssueDto>[] = [
  { header: 'Code', render: row => <code>{row.code}</code> },
  { header: 'Created', render: row => new Date(row.createdAt).toLocaleDateString() },
  { header: 'Updated', render: row => new Date(row.updatedAt).toLocaleDateString() },
]

function IssuePage() {
  const { chosenTenantId } = useAuth()
  const navigate = useNavigate()

  const [page, setPage] = useState(0)
  const [size, setSize] = useState(10)
  const [result, setResult] = useState<PaginationBaseResponse<IssueDto> | null>(null)
  const [loading, setLoading] = useState(false)

  const fetchIssues = useCallback(async () => {
    if (chosenTenantId == null) return
    setLoading(true)
    try {
      const data = await issuesApi.getIssues(chosenTenantId, { page, size })
      setResult(data)
    } finally {
      setLoading(false)
    }
  }, [chosenTenantId, page, size])

  useEffect(() => { fetchIssues() }, [fetchIssues])

  const handleSizeChange = (newSize: number) => {
    setPage(0)
    setSize(newSize)
  }

  if (chosenTenantId == null) {
    return (
      <div className="page">
        <p className="empty-state">Select a tenant to view issues.</p>
      </div>
    )
  }

  return (
    <div className="page">
      <p className="page-title">Issues</p>
      <div className="card">
        <PaginationTable
          columns={COLUMNS}
          data={result?.content ?? []}
          totalElements={result?.totalElements ?? 0}
          totalPages={result?.totalPages ?? 0}
          page={page}
          size={size}
          loading={loading}
          onPageChange={setPage}
          onSizeChange={handleSizeChange}
          onRowClick={row => navigate(`/issues/${chosenTenantId}/${row.id}`)}
        />
      </div>
    </div>
  )
}

export default IssuePage
