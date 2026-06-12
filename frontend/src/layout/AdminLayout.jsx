import Sidebar from "@/components/admin/sidebar/Sidebar";
import { AuthContext } from "@/context/AuthContext";
import { useContext } from "react";
import { Outlet } from "react-router-dom";

export default function AdminLayout() {
    const { token, role } = useContext(AuthContext)

    if(!token || role !== "ROLE_ADMIN") {
        return (
            <div className="text-danger mt-5 text-center">
                Access Denied
            </div>
        )
    }

    return (
        <div className="d-flex min-vh-100" id="wrapper">
            <Sidebar />
            <div id="page-content-wrapper" className="flex-grow-1 bg-light-subtle">
                <div className="container-fluid py-3 py-md-4">
                    <Outlet />
                </div>
            </div>
        </div>
    )
}