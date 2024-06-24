import { configureStore } from "@reduxjs/toolkit";
import traceableReducer from './traceableSlice';

const store = configureStore({
  reducer: {
    traceable: traceableReducer
  }
});

export default store;