import { useState } from 'react';
import TraceableButton from '../components/TraceableButton';
import { getActiveSpan } from '@opentelemetry/api/build/src/trace/context-utils';

function CheckoutPage() {

    const [amount, setAmount] = useState(0);
    const [result, setResult] = useState('');
    const [imgSrc, setImgSrc] = useState('/public/vite.svg');

    const handleSubmit = (e) => {
        e.stopPropagation();
        e.nativeEvent.stopImmediatePropagation();
        e.preventDefault();
        console.log("real button click: ", e.target);
        //fetch('https://api.publicapis.org/entries');
        var traceId = getActiveSpan()?.spanContext().traceId;
        console.log("Trace Id: " + traceId);
        var data = { 'amount': amount, traceId: traceId };
        // You can pass formData as a fetch body directly:
        fetch('http://localhost:9102/checkout?tid=' + traceId, {
            method: 'POST',
            mode: "cors",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
            response.json().then(data => {
                setResult(JSON.stringify(data));
                setImgSrc('/public/react.svg');
                console.log(JSON.stringify(data));
            });
        }).catch(error => {
            // handle the error
            console.log(error);
        });
    };
    const handleError = (e) => {
        e.stopPropagation();
        e.nativeEvent.stopImmediatePropagation();
        e.preventDefault();
        console.log("real button click: ", e.target);
        //fetch('https://api.publicapis.org/entries');
        var data = { 'amount': amount };
        // You can pass formData as a fetch body directly:
        fetch('http://localhost:9102/preview', {
            method: 'POST',
            mode: "cors",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
            response.json().then(data => {
                setResult(JSON.stringify(data));
                setImgSrc('/public/react.svg');
                console.log(JSON.stringify(data));
            });
        }).catch(error => {
            // handle the error
            console.log(error);
        });
    };
    //
    const handleSubmitEvent = (e) => {
        e.stopPropagation();
        e.nativeEvent.stopImmediatePropagation();
        e.preventDefault();
        console.log("real button click: ", e.target);
        //fetch('https://api.publicapis.org/entries');
        var data = { 'amount': amount };
        // You can pass formData as a fetch body directly:
        fetch('http://localhost:9102/checkout/event', {
            method: 'POST',
            mode: "cors",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
            response.json().then(data => {
                setResult(JSON.stringify(data));
                setImgSrc('/public/react.svg');
                console.log(JSON.stringify(data));
            });
        }).catch(error => {
            // handle the error
            console.log(error);
        });
    };
    //
    const handleResultChange = (e) => {

    };
    //
    const handleAmountChange = (e) => {
        e.stopPropagation();
        setAmount(e.target.value);
    }
    const handleAmountClick = (e) => {
        console.log('input click');
    }

    return (
        <>
            <h2>Checkout Page</h2>
            <div className="card">
                <label>
                    Checkout Amount: <input value={amount} onChange={handleAmountChange} onClick={handleAmountClick} />
                </label>
                <hr />
                <TraceableButton id='btnSubmitCheckout' onClick={handleSubmit} >Submit Checkout</TraceableButton>
                <p>
                    <textarea id='txtResult' placeholder='result will be show' value={result} onChange={handleResultChange}></textarea>
                </p>
                <TraceableButton id='btnPreview' onClick={handleError} >Error Preview</TraceableButton>
                <div style={{"paddingTop": "20px"}}>
                    <TraceableButton id='btnSubmitEvent' onClick={handleSubmitEvent} >Submit Checkout Event</TraceableButton>
                </div>
            </div>
            <p className="read-the-docs">
                <img src={imgSrc} />
            </p>
        </>
    );
}

export default CheckoutPage;