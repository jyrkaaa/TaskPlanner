import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useEffect } from "react";
import { useAlert } from "../context/AlertContext";

function IssueDetailsPage() {
  const { tenantId, id } = useParams<{ tenantId: string, id: string }>();
  const { permissions } = useAuth();
  const {info} = useAlert();
  const navigate = useNavigate();

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
  }, [permissions, tenantId]);
  
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