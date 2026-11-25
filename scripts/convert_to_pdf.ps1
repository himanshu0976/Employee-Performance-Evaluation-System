$pptxPath = "c:\Users\hp\Downloads\EPS\Employee_Performance_Evaluation_System_Review2.pptx"
$pdfPath = "c:\Users\hp\Downloads\EPS\Employee_Performance_Evaluation_System_Review2.pdf"

Write-Host "Opening PowerPoint COM application..."
$ppt = New-Object -ComObject PowerPoint.Application

try {
    # Open presentation: ReadOnly=msoTrue, Untitled=msoFalse, WithWindow=msoFalse
    $pres = $ppt.Presentations.Open($pptxPath, 1, 0, 0)
    Write-Host "Exporting to PDF: $pdfPath"
    $pres.SaveAs($pdfPath, 32) # 32 = ppSaveAsPDF
    $pres.Close()
    Write-Host "Export completed successfully!"
} catch {
    Write-Error "Error during conversion: $_"
    exit 1
} finally {
    $ppt.Quit()
    [System.Runtime.Interopservices.Marshal]::ReleaseComObject($ppt) | Out-Null
    [System.GC]::Collect()
    [System.GC]::WaitForPendingFinalizers()
}

Get-Item $pptxPath, $pdfPath | Select-Object Name, Length, LastWriteTime | Format-Table -AutoSize
