import axios from "axios"

const BASE_URL = 'http://localhost:1200/tasty-town/api/v1/foods'

export const fetchPaginatedFoods = async (pageNumber = 0, pageSize = 12, filters = {}) => {
  let url = `${BASE_URL}/paginated-foods?page=${pageNumber}&size=${pageSize}`;

  if (filters.categoryId && filters.categoryId !== 'all') {
    url += `&catId=${filters.categoryId}`
  }

  if (filters.search) {
    url += `&search=${filters.search}`
  }

  const response = await axios.get(url);
  return response.data;
}

export const fetchFoodImage = async (foodImageName) => {
  const url = `${BASE_URL}/${foodImageName}/image`
  const response = await axios.get(url, {
    responseType: "blob"
  })
  return response.data;
}

export const fetchFoodById = async(foodId) => {
  const url = `${BASE_URL}/${foodId}`
  const response = await axios.get(url)
  return response.data
} 

export const fetchFoods = async () => {
  let url = `${BASE_URL}`;
  const response = await axios.get(url);
  return response;
}

export const deleteFoodById = async (token, foodId) => {
  const response = await axios.delete(`${BASE_URL}/${foodId}`, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  });

  return response;
}


export const createFood = async (token, food, foodImage = null) => {
  const formData = new FormData();
  formData.append("foodData", JSON.stringify(food));

  if (foodImage) {
    formData.append("foodImage", foodImage);
  }

  const response = await axios.post(BASE_URL, formData, {
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "multipart/form-data"
    }
  });

  return response;
}

export const uploadFoodImage = async (token, foodId, foodImage) => {
  const formData = new FormData();
  formData.append("foodImage", foodImage);

  const response = await axios.post(`${BASE_URL}/image/${foodId}/food`, formData, {
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "multipart/form-data"
    }
  });

  return response;
}

