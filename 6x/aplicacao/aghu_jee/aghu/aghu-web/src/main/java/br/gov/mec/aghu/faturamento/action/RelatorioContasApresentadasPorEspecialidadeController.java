package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ContaApresentadaPacienteProcedimentoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


/**
 * @author felipe.rocha
 */	
public class RelatorioContasApresentadasPorEspecialidadeController extends ActionReport {
	
	private static final long serialVersionUID = -1006442888234676013L;

	private static final Log LOG = LogFactory.getLog(RelatorioContasApresentadasPorEspecialidadeController.class);
	
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	private AghEspecialidades especialidade; 
	private FatCompetencia competencia;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioContasApresentadasPorEspecialidade.jasper";
	}

	public void print() throws BaseException, JRException, SystemException, IOException {
	
			recuperarColecao();
		}

	public String visualizarRelatorio() {
		return "relatorioContasApresentadasPorEspecialidadePdf";
	}
	
	@Override
	public Collection<ContaApresentadaPacienteProcedimentoVO> recuperarColecao() throws ApplicationBusinessException {
		return faturamentoFacade.obterContaApresentadaEspecialidade(getEspecialidade() != null ? getEspecialidade().getSeq(): null, getCompetencia());
	}
	
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		AghParametros nomeHospital = null;
		nomeHospital = razaoSocia();
		if (nomeHospital != null) {
			params.put("nomeHospital", nomeHospital.getVlrTexto());
		}
		params.put("dataAtual", new Date());
		params.put("nomeRelatorio", "FATR_INT_ESPEC_MES");
		params.put("mesAno", mesAnoAtual());
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/faturamento/report/");
		return params;
	}
	
	public String mesAnoAtual(){
		return 	nomeDoMes(competencia.getId().getMes()) + " / "+ competencia.getId().getAno();
	}
	
	public AghParametros razaoSocia(){
		AghParametros nomeHospital = null;
		try {
			nomeHospital = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);	
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return nomeHospital;
	}
	
	private static String nomeDoMes(int i) { 
		String mes[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
			return(mes[i-1]);
	}
	
	
	public DocumentoJasper buscarDocumentoGerado() throws ApplicationBusinessException{
		return this.gerarDocumento();
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
		
	}

	public String voltar() {
		return "relatorioContaApresentadaEspMes";
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}

}