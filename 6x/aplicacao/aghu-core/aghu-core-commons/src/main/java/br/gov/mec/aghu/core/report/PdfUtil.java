package br.gov.mec.aghu.core.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfBoolean;
import com.itextpdf.text.pdf.PdfEncryptor;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;

/**
 * Classe utilitária para manipulação de PDFs.
 */
public class PdfUtil {

	/**
	 * Proteje o PDF retirando todas as permissões e ocultando barra de menu e
	 * barra de ferramentas. <br />
	 * Retirando as permissões o botão de impressão do leitor de PDF fica
	 * protegido.<br />
	 * Se o PDF já estiver criptografado e protegido de impressão retorna sem
	 * modificações.
	 * 
	 * @param byteArray
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static final ByteArrayOutputStream protectPdf(byte[] byteArray)
			throws IOException, DocumentException {
		if (byteArray == null) {
			throw new IllegalArgumentException("byteArray não pode ser null.");
		}

		if (isImpressaoProtegida(byteArray)) {
			// como já está protegido, retorna o próprio recebido
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			IOUtils.write(byteArray, outputStream);
			return outputStream;
		}

		PdfReader reader = new PdfReader(byteArray);
		// retira todas as permissoes
		int permissions = 0;
		// oculta a menu bar
		reader.addViewerPreference(PdfName.HIDEMENUBAR, PdfBoolean.PDFTRUE);
		// oculta a tool bar
		reader.addViewerPreference(PdfName.HIDETOOLBAR, PdfBoolean.PDFTRUE);

		reader.addViewerPreference(PdfName.HIDE, PdfBoolean.PDFFALSE);
		reader.addViewerPreference(PdfName.HIDEWINDOWUI, PdfBoolean.PDFFALSE);

		// encrypt e sai para byte array
		ByteArrayOutputStream outputStreamModified = new ByteArrayOutputStream();
		PdfEncryptor.encrypt(reader, outputStreamModified, null, null,
				permissions, true);
		reader.close();

		return outputStreamModified;
	}

	/**
	 * Proteje o PDF retirando todas as permissões e ocultando barra de menu e
	 * barra de ferramentas. <br />
	 * Retirando as permissões o botão de impressão do leitor de PDF fica
	 * protegido.<br />
	 * Se o PDF já estiver criptografado e protegido de impressão retorna sem
	 * modificações.
	 * 
	 * @param outputStream
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static final ByteArrayOutputStream protectPdf(
			ByteArrayOutputStream outputStream) throws IOException,
			DocumentException {
		if (outputStream == null) {
			throw new IllegalArgumentException(
					"outputStream não pode ser null.");
		}
		return PdfUtil.protectPdf(outputStream.toByteArray());
	}

	/**
	 * Retorna true se a impressão do PDF é protegida.
	 * 
	 * @param output
	 * @return
	 */
	public static final boolean isImpressaoProtegida(
			ByteArrayOutputStream output) {

		return isImpressaoProtegida(output.toByteArray());
	}

	/**
	 * Retorna true se a impressão do PDF é protegida.
	 * 
	 * @param output
	 * @return
	 */
	public static final boolean isImpressaoProtegida(byte[] byteArray) {
		PdfReader reader = null;
		try {
			reader = new PdfReader(byteArray);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"O argumento não é um pdf válido", e);
		}

		return reader.isEncrypted()
				&& !PdfEncryptor.isPrintingAllowed(reader.getPermissions());
	}
}
