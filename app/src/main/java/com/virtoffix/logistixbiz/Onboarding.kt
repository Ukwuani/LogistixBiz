import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.virtoffix.logistixbiz.FullscreenActivity
import com.virtoffix.logistixbiz.R
import com.virtoffix.logistixbiz.databinding.OnboardBinding

/**
 * @author UKB
 */
class Onboarding(val listener: UrlListener) : DialogFragment() {
    private lateinit var binding: OnboardBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_LogistixBiz_Fullscreen)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK
                && event.action == KeyEvent.ACTION_UP) {
                    requireActivity().finish()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        return dialog
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OnboardBinding.inflate(inflater, container,false)

        binding.loginBtn.setOnClickListener{
            binding.loginBtn.visibility = View.GONE
            binding.progressCircular.visibility = View.VISIBLE
            val alias = binding.alias.text.toString().trim().replace(" ","").replace(".logistix.africa", "")
            checkIfBusinessExists(
                "https://logapi.logistixng.com/api/b2b/${alias}/business",
                {
                    binding.loginBtn.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.GONE
                    listener.enteredUrl(alias)
                    requireDialog().dismiss()

                },
                {
                    binding.loginBtn.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.GONE
                    Toast.makeText(requireContext(), "This business does not exist", Toast.LENGTH_LONG ).show()
                }
            )
        }




        binding.signUpBtn.setOnClickListener{
            listener.register()
            requireDialog().dismiss()
        }
        return binding.root
    }

    fun checkIfBusinessExists(url: String, success: (response: String) -> Unit, failure: () -> Unit) {
        val queue = Volley.newRequestQueue(requireContext())
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                success.invoke(response)
            },
            {
                failure.invoke()
            })

        queue.add(stringRequest)
    }




    companion object {
        @JvmStatic
        fun newInstance(listener: () -> UrlListener): DialogFragment {
            return Onboarding(listener.invoke())
        }
    }


    interface UrlListener {
        fun enteredUrl(data: String)
        fun register(){}
    }





}