import { createSlice } from '@reduxjs/toolkit'

export const traceableSlice = createSlice({
  name: 'traceable',
  initialState: {
    value: false
  },
  reducers: {
    enableTrace: state => {
      // Redux Toolkit allows us to write "mutating" logic in reducers. It
      // doesn't actually mutate the state because it uses the Immer library,
      // which detects changes to a "draft state" and produces a brand new
      // immutable state based off those changes
      state.value = true
    },
    disableTrace: state => {
      state.value = false
    }
  }
})

// Action creators are generated for each case reducer function
export const { enableTrace, disableTrace } = traceableSlice.actions

export default traceableSlice.reducer