import { AuthContext } from "@/context/AuthContext";
import { CategoryContext } from "@/context/CategoryContext";
import { deleteCategoryById } from "@/service/categoryService";
import { useContext, useState } from "react";
import { toast } from "react-toastify";

export default function CategoryList() {
    const { categories, setCategories } = useContext(CategoryContext)
    const { token } = useContext(AuthContext)
    const [deletingCategoryId, setDeletingCategoryId] = useState("")
    
    const removeCategory = async (categoryId) => {
        const confirmed = window.confirm("Delete this category? Related foods will also be removed.")
        if(!confirmed) return

        setDeletingCategoryId(categoryId)
        try {
            const res = await deleteCategoryById(token, categoryId)
            if(res.status === 204) {
                toast.success("Category removed")

                setCategories(prev => prev.filter(category => category.categoryId !== categoryId))
            } else {
                toast.error("Something went wrong")
            }
        } catch (error) {
            toast.error("Something went wrong")
        } finally {
            setDeletingCategoryId("")
        }
    }

    return (
        <div className="py-4 fade-slide-in">
            <div className="card border-0 shadow-lg">
                <div className="card-body p-3 p-md-4">
                    <div className="d-flex justify-content-between align-items-center mb-3">
                        <h4 className="mb-0 fw-bold">Categories</h4>
                        <span className="badge text-bg-dark">{categories.length} Total</span>
                    </div>

                    {!categories.length ? (
                        <p className="text-center text-muted mb-0 py-5">No categories found.</p>
                    ) : (
                        <div className="table-responsive">
                            <table className="table table-hover align-middle mb-0">
                                <thead>
                                    <tr>
                                        <th>Name</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {categories.map((category) => (
                                        <tr key={category.categoryId}>
                                            <td className="fw-semibold">{category.categoryName}</td>
                                            <td>
                                                <button
                                                    type="button"
                                                    className="btn btn-sm btn-outline-danger"
                                                    disabled={deletingCategoryId === category.categoryId}
                                                    onClick={() => removeCategory(category.categoryId)}
                                                >
                                                    <i className="bi bi-trash-fill me-1"></i>
                                                    Delete
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </div>
    )
}