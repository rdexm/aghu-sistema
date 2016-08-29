package br.gov.mec.aghu.parametrosistema.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import net.sf.jasperreports.engine.JRException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.parametrosistema.vo.AghParametroVO;
import br.gov.mec.aghu.report.AghuReportGenerator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * @author rcorvalao
 * 
 */
public class ParametroListReportGenerator extends AghuReportGenerator {
	
	
	private static final long serialVersionUID = -7463045264948935499L;

	private List<AghParametroVO> parametros = new ArrayList<AghParametroVO>();

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/parametrosistema/report/relatorioParametrosSistema.jasper";
	}

	/**
	 * Recupera a coleção utilizada no relatóro
	 */
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return getParametros();
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf.format(new Date()));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AGH_PARAMETROS_REL");
		params.put("tituloRelatorio", "Relatório de Parametros de Sistema");

		return params;

	}

	public byte[] gerar() throws BaseException, JRException, IOException, DocumentException {
		DocumentoJasper documento = super.gerarDocumento();

		return documento.getPdfByteArray(false);
	}

	public List<AghParametroVO> getParametros() {
		return parametros;
	}

	public void setParametros(List<AghParametroVO> parametros) {
		this.parametros = parametros;
	}

}
