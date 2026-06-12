import { AuthContext } from '@/context/AuthContext';
import { deleteFoodById, fetchFoods } from '@/service/foodService';
import React, { useContext, useEffect, useState } from 'react'
import { toast } from 'react-toastify';

const FoodsList = () => {
    const [foods, setFoods] = useState([])
    const [loading, setLoading] = useState(false)
    const [deletingFoodId, setDeletingFoodId] = useState("")
    const { token } = useContext(AuthContext)

    const BASE_URL = 'http://localhost:1200/tasty-town/api/v1/foods'

    const loadFoods = async () => {
        setLoading(true)
        try {
            const res = await fetchFoods();
            if (res.status === 200) {
                setFoods(res.data)
            } else {
                toast.error("Something went wrong")
            }
        } catch (error) {
            toast.error("Something went wrong")
        } finally {
            setLoading(false)
        }
    };

    const removeFood = async (foodId, foodName) => {
        if(!token) {
            toast.error("Please login as admin")
            return
        }

        const confirmed = window.confirm(`Delete ${foodName}?`)
        if(!confirmed) return

        setDeletingFoodId(foodId)
        try {
            const res = await deleteFoodById(token, foodId)

            if (res.status === 204) {
                setFoods(prev => prev.filter(food => food.foodId !== foodId))
                toast.success("Food deleted")
            } else {
                toast.error("Something went wrong")
            }
        } catch (error) {
            toast.error("Something went wrong")
        } finally {
            setDeletingFoodId("")
        }
    };

    useEffect(() => {
        loadFoods()
    }, [])

    return (
        <div className="py-4 fade-slide-in">
            <div className="card border-0 shadow-lg">
                <div className="card-body p-3 p-md-4">
                    <div className="d-flex justify-content-between align-items-center mb-3">
                        <h4 className="mb-0 fw-bold">Foods</h4>
                        <span className="badge text-bg-dark">{foods.length} Total</span>
                    </div>

                    {loading ? (
                        <p className="text-center text-muted mb-0 py-5">Loading foods...</p>
                    ) : !foods.length ? (
                        <p className="text-center text-muted mb-0 py-5">No foods found.</p>
                    ) : (
                        <div className="table-responsive">
                            <table className="table table-hover align-middle mb-0">
                                <thead>
                                    <tr>
                                        <th>Image</th>
                                        <th>Name</th>
                                        <th>Category</th>
                                        <th>Price</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {foods.map((food) => (
                                        <tr key={food.foodId}>
                                            <td>
                                                {food.foodImage ? (
                                                    <img
                                                        src={`${BASE_URL}/${food.foodImage}/image`}
                                                        alt={food.foodName}
                                                        height="48"
                                                        width="48"
                                                        className="rounded object-fit-cover"
                                                    />
                                                ) : (
                                                    <div className="d-inline-flex rounded border bg-light justify-content-center align-items-center"
                                                        style={{ width: 48, height: 48 }}
                                                    >
                                                        <i className="bi bi-image text-muted"></i>
                                                    </div>
                                                )}
                                            </td>

                                            <td className="fw-semibold">{food.foodName}</td>
                                            <td>{food.categoryName}</td>
                                            <td>₹ {Number(food.foodPrice || 0).toFixed(2)}</td>
                                            <td>
                                                <button
                                                    type="button"
                                                    className="btn btn-sm btn-outline-danger"
                                                    disabled={deletingFoodId === food.foodId}
                                                    onClick={() => removeFood(food.foodId, food.foodName)}
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

export default FoodsList
