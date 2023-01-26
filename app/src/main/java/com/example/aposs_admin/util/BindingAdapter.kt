package com.example.aposs_admin.util

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.*
import com.example.aposs_admin.model.*
import com.example.aposs_admin.model.dto.KindDTO
import com.example.aposs_admin.model.dto.PredictionRecordDTO
import me.relex.circleindicator.CircleIndicator3

@BindingAdapter("billingItemData")
fun bindBillingItemRecyclerView(
    recyclerView: RecyclerView,
    data: ArrayList<OrderBillingItem?>
) {
    val adapter = (recyclerView.adapter as BillingItemsAdapter?)!!
    adapter.submitList(data)
}

@BindingAdapter("image")
fun bindImage(imageView: ImageView, image: Uri?) {
    Glide.with(imageView.context)
        .load(image)
        .apply(
            RequestOptions().placeholder(R.drawable.animation_loading)
        )
        .into(imageView)
    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
}

@BindingAdapter("statusIconData")
fun bindStatusIcon(imageView: ImageView, data: OrderStatus?) {
    if (data != null) {
        when (data) {
            OrderStatus.Success -> {
                imageView.setImageResource(R.drawable.ic_order_pass)
            }
            OrderStatus.Pending -> {
                imageView.setImageResource(R.drawable.ic_order_pending)
            }
            OrderStatus.Cancel -> {
                imageView.setImageResource(R.drawable.ic_order_cancel)
            }
            OrderStatus.Confirmed -> {
                imageView.setImageResource(R.drawable.ic_order_confirm)
            }
            OrderStatus.Delivering -> {
                imageView.setImageResource(R.drawable.ic_order_delivering)
            }
        }
    }
}

@BindingAdapter("orderData")
fun bindOrderRecyclerView(recyclerView: RecyclerView, orders: ArrayList<Order?>) {
    val adapter = (recyclerView.adapter as OrderAdapter?)!!
    adapter.submitList(orders)
}

@BindingAdapter("products")
fun bindProductsRecyclerView(recyclerView: RecyclerView, products: List<HomeProduct>?) {
    val adapter = (recyclerView.adapter as ProductsAdapter)
    adapter.submitList(products)
}

@BindingAdapter("imagesData")
fun bindDetailProductImageViewPager(viewPager2: ViewPager2, data: List<Image>?){
    val adapter = viewPager2.adapter as DetailProductImageViewPagerAdapter
    adapter.submitList(data)
}
@BindingAdapter( "indicatorSize")
fun bindIndicatorSize(indicator: CircleIndicator3, size: Int){
    indicator.createIndicators(size , 0)
}

@BindingAdapter("list_images")
fun bindListImagesRecyclerView(recyclerView: RecyclerView, images: ArrayList<LocalImage>?) {
    val adapter = (recyclerView.adapter as AddImageAdapter)
    adapter.submitList(images)
}

@BindingAdapter("list_category")
fun bindListCategoryRecyclerView(recyclerView: RecyclerView, categoryNames: List<String>?){
    val adapter = recyclerView.adapter as CategoryAndSubcategoryAdapter
    adapter.submitList(categoryNames)
}

@BindingAdapter("list_kind")
fun bindListKindRecyclerView(recyclerView: RecyclerView, kinds: List<KindDTO>?){
    val adapter = recyclerView.adapter as KindAdapter
    adapter.submitList(kinds)
}

@BindingAdapter("list_predict")
fun bindListPredictRecyclerView(recyclerView: RecyclerView, predict: List<PredictionRecordDTO>?){
    val adapter = recyclerView.adapter as PredictionRecordAdapter
    adapter.submitList(predict)
}


@BindingAdapter("category_images")
fun bindListCategoryImageRecyclerView(recyclerView: RecyclerView, images: List<Image>?){
    val adapter = recyclerView.adapter as CategoryAndSubCategoryImageAdapter
    adapter.submitList(images)
}

@BindingAdapter("totalItemInformation")
fun bindingAdditionalText(textView:TextView, additionalInformation: String?){
    if (additionalInformation != null){
        textView.text = "Danh sách hình ảnh"
        val textViewText = textView.text.toString() + " (" + additionalInformation + "):"
        textView.text = textViewText
    }
}
@BindingAdapter("list_account")
fun bindListAccountRecyclerView(recyclerView: RecyclerView, account: List<String>?){
    val adapter = recyclerView.adapter as AccountAdapter
    adapter.submitList(account)
}

@BindingAdapter("list_calendars")
fun bindListCalendarRecyclerView(recyclerView: RecyclerView, calendarItems: List<CalendarItem>?){
    val adapter = recyclerView.adapter as CalendarAdapter
    adapter.submitList(calendarItems)
}

@BindingAdapter("isSpecial")
fun bindIsSpecialDateImageView(imageView: ImageView, isSpecial: Boolean){
    if (isSpecial) {
        imageView.visibility = View.VISIBLE
    } else {
        imageView.visibility = View.GONE
    }
}