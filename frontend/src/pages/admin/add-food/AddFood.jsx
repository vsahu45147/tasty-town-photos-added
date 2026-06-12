import { AuthContext } from "@/context/AuthContext";
import { CategoryContext } from "@/context/CategoryContext";
import { createFood } from "@/service/foodService";
import { useContext, useState } from "react";
import { toast } from "react-toastify";

const initialForm = {
	foodName: "",
	foodDescription: "",
	foodPrice: "",
	categoryId: ""
};

export default function AddFood() {
	const { token } = useContext(AuthContext);
	const { categories } = useContext(CategoryContext);

	const [formData, setFormData] = useState(initialForm);
	const [foodImage, setFoodImage] = useState(null);
	const [loading, setLoading] = useState(false);

	const onChangeHandler = (event) => {
		const { name, value } = event.target;
		setFormData((prev) => ({ ...prev, [name]: value }));
	};

	const validateForm = () => {
		if (!formData.foodName.trim()) {
			toast.error("Food name is required");
			return false;
		}

		if (!formData.foodDescription.trim()) {
			toast.error("Food description is required");
			return false;
		}

		if (formData.foodDescription.trim().length > 100) {
			toast.error("Description must be 100 characters or less");
			return false;
		}

		if (!formData.foodPrice || Number(formData.foodPrice) < 0) {
			toast.error("Food price must be a positive value");
			return false;
		}

		if (!formData.categoryId) {
			toast.error("Please select category");
			return false;
		}

		return true;
	};

	const onSubmitHandler = async (event) => {
		event.preventDefault();

		if (!token) {
			toast.error("Please login as admin");
			return;
		}

		if (!validateForm()) return;

		setLoading(true);

		const payload = {
			foodName: formData.foodName.trim(),
			foodDescription: formData.foodDescription.trim(),
			foodPrice: Number(formData.foodPrice),
			categoryId: formData.categoryId
		};

		try {
			const response = await createFood(token, payload, foodImage);

			if (response.status !== 201) {
				toast.error("Unable to add food");
				return;
			}

			setFormData(initialForm);
			setFoodImage(null);
			toast.success("Food added successfully");
		} catch (error) {
			const message = error?.response?.data?.message || "Unable to add food";
			toast.error(message);
		} finally {
			setLoading(false);
		}
	};

	return (
		<div className="py-5 row justify-content-center fade-slide-in">
			<div className="col-11 col-lg-8">
				<div className="card shadow-lg border-0">
					<div className="card-body p-4 p-md-5">
						<h4 className="fw-bold mb-1">
							<i className="bi bi-plus-circle me-2"></i>
							Add Food
						</h4>
						<p className="text-muted mb-4">Create a new food item and optionally upload an image.</p>

						<form onSubmit={onSubmitHandler}>
							<div className="row g-3">
								<div className="col-md-6">
									<label htmlFor="foodName" className="form-label">Food Name</label>
									<input
										id="foodName"
										name="foodName"
										type="text"
										className="form-control"
										value={formData.foodName}
										onChange={onChangeHandler}
										maxLength={50}
										placeholder="e.g. Paneer Tikka"
									/>
								</div>

								<div className="col-md-6">
									<label htmlFor="foodPrice" className="form-label">Price</label>
									<input
										id="foodPrice"
										name="foodPrice"
										type="number"
										className="form-control"
										value={formData.foodPrice}
										onChange={onChangeHandler}
										step="0.01"
										min="0"
										placeholder="e.g. 199"
									/>
								</div>

								<div className="col-12">
									<label htmlFor="foodDescription" className="form-label">Description</label>
									<textarea
										id="foodDescription"
										name="foodDescription"
										className="form-control"
										rows="3"
										value={formData.foodDescription}
										onChange={onChangeHandler}
										maxLength={100}
										placeholder="Short food description"
									/>
								</div>

								<div className="col-md-6">
									<label htmlFor="categoryId" className="form-label">Category</label>
									<select
										id="categoryId"
										name="categoryId"
										className="form-select"
										value={formData.categoryId}
										onChange={onChangeHandler}
									>
										<option value="">Select category</option>
										{categories.map((category) => (
											<option key={category.categoryId} value={category.categoryId}>
												{category.categoryName}
											</option>
										))}
									</select>
								</div>

								<div className="col-md-6">
									<label htmlFor="foodImage" className="form-label">Food Image (optional)</label>
									<input
										id="foodImage"
										type="file"
										className="form-control"
										accept="image/png,image/jpeg,image/jpg"
										onChange={(event) => setFoodImage(event.target.files?.[0] || null)}
									/>
								</div>
							</div>

							<div className="d-flex gap-2 mt-4">
								<button type="submit" className="btn btn-success" disabled={loading}>
									{loading ? "Saving..." : "Add Food"}
								</button>
								<button
									type="button"
									className="btn btn-outline-secondary"
									disabled={loading}
									onClick={() => {
										setFormData(initialForm);
										setFoodImage(null);
									}}
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
