package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.vo.RelatorioEscalaProfissionaisSemanaVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;


public class RelatorioEscalaProfissionaisSemanaController extends ActionReport {

	private static final String _HIFEN_ = " - ";

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1006442888234676013L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	private static final String RELATORIO_ESCALA = "relatorioEscalaProfissionaisSemana";
	private static final String RELATORIO_ESCALA_PDF = "relatorioEscalaProfissionaisSemanaPdf";

	
	private static final Log LOG = LogFactory.getLog(RelatorioEscalaProfissionaisSemanaController.class);
	
	private AghUnidadesFuncionais unidadeFuncional;
	private MbcTurnos turnos;
	
	private List<RelatorioEscalaProfissionaisSemanaVO> colecao;
	
	private DominioFuncaoProfissional funcaoProfissional1;
	private DominioFuncaoProfissional funcaoProfissional2;
	private DominioFuncaoProfissional funcaoProfissional3;
	private DominioFuncaoProfissional funcaoProfissional4;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio(){
	 

	 

		
		if(unidadeFuncional == null){
			unidadeFuncional = new AghUnidadesFuncionais();
		}
	
		/* Carrega Suggestion "Unidade Cirurgica" com a unidade cadastrada no computador */
		this.setUnidadeFuncional(this.carregarUnidadeFuncionalInicial());
	
	}
	
	
	/* Obtem unidade cirurgica cadastrada para o computador */
	private AghUnidadesFuncionais carregarUnidadeFuncionalInicial() {
		String nomeMicrocomputador = null;
			
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			return blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e1) {
			LOG.error("Exceção capturada:", e1);
		}
		return null;
	}
	
	/* Suggestion "Unidade Cirurgica" */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(String objUnidadeFuncional) {
		return this.returnSGWithCount(blocoCirurgicoCadastroFacade.buscarUnidadesFuncionaisCirurgia(objUnidadeFuncional), 
				blocoCirurgicoCadastroFacade.contarUnidadesFuncionaisCirurgia(objUnidadeFuncional));
	}
	
	/* Suggestion "Turnos" */
	public List<MbcTurnos> pesquisarTurnos(String objTurnos){
		return this.returnSGWithCount(blocoCirurgicoCadastroFacade.pesquisarTiposTurno(objTurnos),
				blocoCirurgicoCadastroFacade.pesquisarTiposTurnoCount(objTurnos));
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioEscalaProfissionaisSemana.jasper";
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		try {	
			AghParametros parametroRazaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			parametros.put("hospitalLocal", parametroRazaoSocial.getVlrTexto());
			
			parametros.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
			
			String unidadeTurno = this.blocoCirurgicoCadastroFacade.pesquisarUnidadeTurno(unidadeFuncional.getSeq(), turnos.getTurno());
			parametros.put("unidadeTurno", unidadeTurno);
			
			if(funcaoProfissional1 != null){
				parametros.put("funcao1", funcaoProfissional1.getCodigo() + _HIFEN_+ funcaoProfissional1.getDescricao());
			}
			if(funcaoProfissional2 != null){
				parametros.put("funcao2", funcaoProfissional2.getCodigo() + _HIFEN_+ funcaoProfissional2.getDescricao());
			}
			if(funcaoProfissional3 != null){
				parametros.put("funcao3", funcaoProfissional3.getCodigo() + _HIFEN_+ funcaoProfissional3.getDescricao());
			}
			if(funcaoProfissional4 != null){
				parametros.put("funcao4", funcaoProfissional4.getCodigo() + _HIFEN_+ funcaoProfissional4.getDescricao());
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return parametros;
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioEscalaProfissionaisSemanaVO> recuperarColecao() throws ApplicationBusinessException {		
		return colecao; 
	}
	
	/* Método Buscar - Dados do Relatório */
	public void carregarColecao() throws ApplicationBusinessException {		
		colecao = this.blocoCirurgicoCadastroFacade.buscarDadosRelatorioEscalaProfissionaisSemana(unidadeFuncional, 
				turnos, 
				funcaoProfissional1, 
				funcaoProfissional2, 
				funcaoProfissional3, 
				funcaoProfissional4);			 
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));			
	}
	
	/* Visualizar relatório */
	public String print()throws JRException, IOException, DocumentException {
		try {
			carregarColecao();
		
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));			
		return RELATORIO_ESCALA_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String voltar() {
		return RELATORIO_ESCALA;
	}
	
	/* Impressão direta */
	public void directPrint() {
		try {
				carregarColecao();
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	/* Método Limpar  */
	public void limparCampos(){
		this.setTurnos(null);
		this.setFuncaoProfissional1(null);
		this.setFuncaoProfissional2(null);
		this.setFuncaoProfissional3(null);
		this.setFuncaoProfissional4(null);
		inicio();
	}
	
	/* Getters and Setters */
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setTurnos(MbcTurnos turnos) {
		this.turnos = turnos;
	}

	public MbcTurnos getTurnos() {
		return turnos;
	}

	public void setFuncaoProfissional1(DominioFuncaoProfissional funcaoProfissional1) {
		this.funcaoProfissional1 = funcaoProfissional1;
	}

	public DominioFuncaoProfissional getFuncaoProfissional1() {
		return funcaoProfissional1;
	}

	public void setFuncaoProfissional2(DominioFuncaoProfissional funcaoProfissional2) {
		this.funcaoProfissional2 = funcaoProfissional2;
	}

	public DominioFuncaoProfissional getFuncaoProfissional2() {
		return funcaoProfissional2;
	}

	public void setFuncaoProfissional3(DominioFuncaoProfissional funcaoProfissional3) {
		this.funcaoProfissional3 = funcaoProfissional3;
	}

	public DominioFuncaoProfissional getFuncaoProfissional3() {
		return funcaoProfissional3;
	}

	public void setFuncaoProfissional4(DominioFuncaoProfissional funcaoProfissional4) {
		this.funcaoProfissional4 = funcaoProfissional4;
	}

	public DominioFuncaoProfissional getFuncaoProfissional4() {
		return funcaoProfissional4;
	}

}
