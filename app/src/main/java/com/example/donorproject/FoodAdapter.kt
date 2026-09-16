package com.example.donorproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter (
    private val onFoodClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    private var foods: List<Food> = emptyList()

    // Replaces the current adapter list and tells the RecyclerView to redraw its rows
    fun updateFoods(newFoods: List<Food>) {
        foods = newFoods
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food , parent , false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FoodViewHolder,
        position: Int
    ) {
        val food = foods[position]
        holder.bind(food)

        holder.itemView.setOnClickListener {
            onFoodClick(food)
        }
    }

    override fun getItemCount() : Int {
        return foods.size
    }

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodNameText : TextView = itemView.findViewById(R.id.foodNameText)
        private val brandNameText : TextView = itemView.findViewById(R.id.brandNameText)
        private val descriptionText : TextView = itemView.findViewById(R.id.descriptionText)

        fun bind(food : Food) {
            foodNameText.text = food.foodName
            brandNameText.text = food.brandName ?: "Generic food"
            descriptionText.text = food.foodDescription ?: ""
        }
    }
}