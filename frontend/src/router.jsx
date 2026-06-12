import { createBrowserRouter, Navigate } from "react-router-dom";
import App from "./App";
import ExploreFood from "./pages/explore foods/ExploreFood";
import FoodDetails from "./pages/food details/FoodDetails";
import ErrorPage from "./common/ErrorPage";
import Login from "./pages/auth/Login";
import CustomerLayout from "./layout/CustomerLayout";
import Home from "./pages/home/Home";
import Register from "./pages/auth/Register";
import Cart from "./pages/cart/Cart";
import PlaceOrder from "./pages/place order/PlaceOrder";
import Orders from "./pages/ordes/Orders";
import AdminLayout from "./layout/AdminLayout";
import OrdersList from "./pages/admin/orders-list/OrdersList";
import CategoryList from "./pages/admin/category-list/CategoryList";
import FoodsList from "./pages/admin/foods-list/FoodsList";
import AddFood from "./pages/admin/add-food/AddFood";
import AddCategory from "./pages/admin/add-category/AddCategory";

const router = createBrowserRouter([
  {
    path: "/",
    errorElement: <ErrorPage />,
    children: [
      {
        element: <CustomerLayout />,
        children: [
          { index: true, element: <Home /> },
          { path: "explore", element: <ExploreFood /> },
          { path: "food/:foodId", element: <FoodDetails /> },
          { path: "login", element: <Login /> },
          { path: "register", element: <Register /> },
          { path: "cart", Component: Cart },
          { path: "place-order", Component: PlaceOrder },
          { path: "orders", Component: Orders }
        ]
      }
    ]
  },
  {
    path: "/admin",
    element: <AdminLayout />,
    errorElement: <ErrorPage />,
    children: [
      { index: true, element: <Navigate to="orders" replace /> },
      { path: "orders", element: <OrdersList />},
      { path: "add-food", element: <AddFood />},
      { path: "add-category", element: <AddCategory />},
      { path: "categories", element: <CategoryList />},
      { path: "foods", element: <FoodsList />},
    ]
  }
])

export default router;