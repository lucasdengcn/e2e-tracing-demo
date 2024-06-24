import { MouseEventHandler } from "react";

function TraceableButton({id, onClick, children}) {

    const handleSubmit = (e) => {

    }

    return (
        <button type='button' id={id} traceable="true" onClick={onClick}>{children}</button>
    );
}

export default TraceableButton;