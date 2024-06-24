import { useState } from 'react';
import Gallery from '../components/Gallery';


function ScientistPage() {
    return (
        <>
            <h2>Scientist Page</h2>
            <div className="card">
                <Gallery />
            </div>
        </>
    );
}

export default ScientistPage;