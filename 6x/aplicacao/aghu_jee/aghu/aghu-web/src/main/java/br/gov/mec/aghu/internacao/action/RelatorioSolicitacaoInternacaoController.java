package br.gov.mec.aghu.internacao.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.action.PesquisaSolicitacaoInternacaoPaginatorController;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.internacao.vo.RelatorioSolicitacaoInternacaoVO;
import br.gov.mec.aghu.internacao.vo.ServidorConselhoVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioSolicitacaoInternacaoController extends ActionReport {

	private static final long serialVersionUID = -8268929005169723473L;
	private static final String REDIRECT_PESQUISA_SOLICITACAO_INTERNACAO = "internacao-pesquisaSolicitacaoInternacao";

	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private PesquisaSolicitacaoInternacaoPaginatorController pesquisaSolicitacaoInternacaoPaginatorController;
	
	private AghClinicas clinicaPesquisa;

	private ServidorConselhoVO servidorConselhoVOPesquisa;
	
	private ConvenioPlanoVO convenioPlanoVOPesquisa;

	private AghEspecialidades especialidadePesquisa;
	
	private AipPacientes pacientePesquisa;
	
	private DominioSituacaoSolicitacaoInternacao indSolicitacaoInternacao;
	
	private Date dataSolicitacao;
	
	private Date dataPrevisao;
	
	private List<RelatorioSolicitacaoInternacaoVO> colecao = new ArrayList<RelatorioSolicitacaoInternacaoVO>(0);

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws BaseException
	 * @throws DocumentException 
	 * @throws ApplicationBusinessException 
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = this.gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public String recuperarArquivoRelatorio(){
		return	"br/gov/mec/aghu/internacao/report/relatorioSolicitacaoInternacao.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		params.put("nomeRelatorio", "AINR_SOLICITACOES_INT");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("hospital", cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());
		if(this.dataSolicitacao!=null){
			params.put("dataPesquisa", sdf.format(this.dataSolicitacao));
		}

		return params;
	}

	public void print()
			throws BaseException, JRException, SystemException, IOException {
	
		try {
			this.colecao = internacaoFacade
					.obterSolicitacoesInternacao(this.dataSolicitacao,
							this.indSolicitacaoInternacao, this.clinicaPesquisa, this.dataPrevisao, this.pacientePesquisa,
							this.servidorConselhoVOPesquisa, this.especialidadePesquisa, this.convenioPlanoVOPesquisa);
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR,
					e.getLocalizedMessage());			
		}
		
	}

	@Override
	public Collection<RelatorioSolicitacaoInternacaoVO> recuperarColecao() {
		return this.colecao;
	}
    
	public String voltar(){
		this.dataSolicitacao = null;
		this.indSolicitacaoInternacao = null;
		this.pesquisaSolicitacaoInternacaoPaginatorController.setDataPrevisao(dataPrevisao);
		this.pesquisaSolicitacaoInternacaoPaginatorController.setIndSolicitacaoInternacao(indSolicitacaoInternacao);
		return REDIRECT_PESQUISA_SOLICITACAO_INTERNACAO;
	}
	
	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public DominioSituacaoSolicitacaoInternacao getIndSolicitacaoInternacao() {
		return indSolicitacaoInternacao;
	}

	public void setIndSolicitacaoInternacao(
			DominioSituacaoSolicitacaoInternacao indSolicitacaoInternacao) {
		this.indSolicitacaoInternacao = indSolicitacaoInternacao;
	}

	public AghClinicas getClinicaPesquisa() {
		return clinicaPesquisa;
	}

	public void setClinicaPesquisa(AghClinicas clinicaPesquisa) {
		this.clinicaPesquisa = clinicaPesquisa;
	}

	public ServidorConselhoVO getServidorConselhoVOPesquisa() {
		return servidorConselhoVOPesquisa;
	}

	public void setServidorConselhoVOPesquisa(
			ServidorConselhoVO servidorConselhoVOPesquisa) {
		this.servidorConselhoVOPesquisa = servidorConselhoVOPesquisa;
	}

	public ConvenioPlanoVO getConvenioPlanoVOPesquisa() {
		return convenioPlanoVOPesquisa;
	}

	public void setConvenioPlanoVOPesquisa(ConvenioPlanoVO convenioPlanoVOPesquisa) {
		this.convenioPlanoVOPesquisa = convenioPlanoVOPesquisa;
	}

	public AghEspecialidades getEspecialidadePesquisa() {
		return especialidadePesquisa;
	}

	public void setEspecialidadePesquisa(AghEspecialidades especialidadePesquisa) {
		this.especialidadePesquisa = especialidadePesquisa;
	}

	public AipPacientes getPacientePesquisa() {
		return pacientePesquisa;
	}

	public void setPacientePesquisa(AipPacientes pacientePesquisa) {
		this.pacientePesquisa = pacientePesquisa;
	}

	public Date getDataPrevisao() {
		return dataPrevisao;
	}

	public void setDataPrevisao(Date dataPrevisao) {
		this.dataPrevisao = dataPrevisao;
	}

	public List<RelatorioSolicitacaoInternacaoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioSolicitacaoInternacaoVO> colecao) {
		this.colecao = colecao;
	}
	
}
