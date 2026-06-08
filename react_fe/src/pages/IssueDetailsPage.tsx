import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useEffect, useState } from "react";
import { useAlert } from "../context/AlertContext";
import { IssueDto } from "../models/dto/IssueDto";
import { issuesApi } from "../api/issues";

function IssueDetailsPage() {
  const { tenantId, id } = useParams<{ tenantId: string, id: string }>();
  const { permissions } = useAuth();
  const {info} = useAlert();
  const navigate = useNavigate();
  const [paginationSize, setPaginationSize] = useState(10);
  const [currentPage, setCurrentPage] = useState(1);
  const [issues, setIssues] = useState<IssueDto[] | null>(null);

  useEffect(() => {
    if (hasFaultyParams() || !permissions) {
      info("Invalid parameters or missing permissions");
      navigate('/');
    }
    const tenantPerms = permissions!.find(p => p.tenantId === parseInt(tenantId!));
    if (!tenantPerms) {
      info("You do not have permissions for this tenant");
      navigate('/');
    }

    issuesApi.getIssues(parseInt(tenantId!), { page: currentPage, size: paginationSize })
      .then(response => setIssues(response.content))
      .catch(error => {
        info("Failed to fetch issues");
        navigate('/');
      });
  }, [permissions, tenantId, navigate, currentPage, paginationSize]);
  
  function hasFaultyParams() {
    return !tenantId || isNaN(parseInt(tenantId)) || !id || isNaN(parseInt(id));
  }
    return (
    <div>
      <h1>Issue Details</h1>
      {/* Issue details content goes here */}
    </div>
  )
}

export default IssueDetailsPage;