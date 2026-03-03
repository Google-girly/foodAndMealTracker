import { createClient } from '@supabase/supabase-js'

export const supabase = createClient(
  'https://peiotjyqfufhdqasyham.supabase.co',
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBlaW90anlxZnVmaGRxYXN5aGFtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzIwNjIzMzUsImV4cCI6MjA4NzYzODMzNX0.x4NOUedtgTjQWuV2yrBKt3ZRgNY3RqtUW2NeWx_iRGA'
)