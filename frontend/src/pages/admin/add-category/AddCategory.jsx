import { AuthContext } from "@/context/AuthContext";
import { CategoryContext } from "@/context/CategoryContext";
import { createCategory } from "@/service/categoryService";
import { useContext, useState } from "react";
import { toast } from "react-toastify";

export default function AddCategory() {
	const { token } = useContext(AuthContext);
	const { setCategories } = useContext(CategoryContext);

	const [categoryName, setCategoryName] = useState("");
	const [loading, setLoading] = useState(false);

	const onSubmitHandler = async (event) => {
		event.preventDefault();

		const name = categoryName.trim();
		if (!name) {
			toast.error("Category name is required");
			return;
		}

		if (!token) {
			toast.error("Please login as admin");
			return;
		}

		setLoading(true);

		try {
			const response = await createCategory(token, name);

			if (response.status === 201) {
				setCategories((prev) => [response.data, ...prev]);
				setCategoryName("");
				toast.success("Category added successfully");
			} else {
				toast.error("Unable to add category");
			}
		} catch (error) {
			const message = error?.response?.data?.message || "Unable to add category";
			toast.error(message);
		} finally {
			setLoading(false);
		}
	};

	return (
		<div className="py-5 row justify-content-center fade-slide-in">
			<div className="col-11 col-lg-7">
				<div className="card shadow-lg border-0">
					<div className="card-body p-4 p-md-5">
						<h4 className="fw-bold mb-1">
							<i className="bi bi-tags me-2"></i>
							Add Category
						</h4>
						<p className="text-muted mb-4">Create a new category for food items.</p>

						<form onSubmit={onSubmitHandler}>
							<div className="form-floating mb-4">
								<input
									id="categoryName"
									type="text"
									className="form-control"
									placeholder="Category Name"
									value={categoryName}
									maxLength={40}
									onChange={(event) => setCategoryName(event.target.value)}
								/>
								<label htmlFor="categoryName">Category Name</label>
							</div>

							<div className="d-flex gap-2">
								<button type="submit" className="btn btn-success" disabled={loading}>
									{loading ? "Adding..." : "Add Category"}
								</button>
								<button
									type="button"
									className="btn btn-outline-secondary"
									disabled={loading}
									onClick={() => setCategoryName("")}
								>
									Reset
								</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	);
}
