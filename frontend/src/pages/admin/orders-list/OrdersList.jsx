import { AuthContext } from "@/context/AuthContext"
import { fetchAllRecentOrders, updateOrderStatus } from "@/service/orderService"
import { useContext, useEffect, useState } from "react"
import { toast } from "react-toastify"

export default function OrdersList() {
  const { token } = useContext(AuthContext)
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(false)
  const [updatingOrderId, setUpdatingOrderId] = useState("")

  const fetchAllOrders = async () => {
    setLoading(true)
    try {
      const response = await fetchAllRecentOrders(token)
      if (response.status === 200) {
        setOrders(response.data)
      }
    } catch (error) {
      toast.error("Something went wrong")
    } finally {
      setLoading(false)
    }
  }

  const updateStatus = async (e, orderId) => {
    setUpdatingOrderId(orderId)
    try {
      const newStatus = e.target.value
      const response = await updateOrderStatus(token, orderId, newStatus)

      if (response.status === 200) {
        setOrders(prevOrders => prevOrders.map(order => (
          order?.orderId === orderId ? { ...order, orderStatus: newStatus } : order
        )))

        toast.success("Order Status Updated")
      }
    } catch (error) {
      toast.error("Something went wrong")
    } finally {
      setUpdatingOrderId("")
    }
  }

  useEffect(() => {
    if (token) {
      fetchAllOrders()
    }
  }, [token])

  const statusLabels = {
    "FOOD_PREPARING": "FOOD PREPARING",
    "OUT_FOR_DELIVERY": "OUT FOR DELIVERY",
    "DELIVERED": "DELIVERED",
  }

  const statusTransitions = {
    "FOOD_PREPARING": ["OUT_FOR_DELIVERY", "DELIVERED"],
    "OUT_FOR_DELIVERY": ["DELIVERED"],
    "DELIVERED": []
  }

  const formatDateTime = (dateString) => {
    if (!dateString) return "N/A"

    const date = new Date(dateString)
    if (Number.isNaN(date.getTime())) return "N/A"

    return date.toLocaleString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit"
    })
  }

  return (
    <div className="py-4 fade-slide-in">
      <div className="card border-0 shadow-lg">
        <div className="card-body p-3 p-md-4">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h4 className="mb-0 fw-bold">Orders</h4>
            <span className="badge text-bg-dark">{orders.length} Total</span>
          </div>

          {loading ? (
            <p className="text-center text-muted mb-0 py-5">Loading orders...</p>
          ) : !orders.length ? (
            <p className="text-center text-muted mb-0 py-5">No orders found.</p>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead>
                  <tr>
                    <th>Order</th>
                    <th>Items</th>
                    <th>Total</th>
                    <th>Customer</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map((order) => (
                    <tr key={order.orderId}>
                      <td>
                        <div className="fw-semibold">#{order.orderId?.slice(-8) || "N/A"}</div>
                        <div className="small text-muted">{formatDateTime(order.orderDate)}</div>
                      </td>

                      <td>
                        <div className="small text-muted">{order.orderItems?.length || 0} item(s)</div>
                        <div>
                          {(order.orderItems || []).map((item, idx) => (
                            <span key={idx} className="badge text-bg-light border me-1 mb-1">
                              {item.foodName} x {item.quantity}
                            </span>
                          ))}
                        </div>
                      </td>

                      <td className="fw-semibold">₹ {Number(order.totalAmount || 0).toFixed(2)}</td>

                      <td>
                        <div className="small"><strong>Contact:</strong> {order.contactInfo || "N/A"}</div>
                        <div className="small text-muted"><strong>Address:</strong> {order.addressInfo || "N/A"}</div>
                      </td>

                      <td>
                        <select
                          className="form-select form-select-sm"
                          value={order.orderStatus}
                          onChange={(e) => updateStatus(e, order.orderId)}
                          disabled={!statusTransitions[order.orderStatus]?.length || updatingOrderId === order.orderId}
                        >
                          <option value={order.orderStatus}>{statusLabels[order.orderStatus]}</option>
                          {statusTransitions[order.orderStatus]?.map((nextStatus) => (
                            <option key={nextStatus} value={nextStatus}>{statusLabels[nextStatus]}</option>
                          ))}
                        </select>
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