package br.gov.mec.aghu.controleinfeccao.action;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.RelatorioNotificGermeMultirresistenteVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MciBacteriaMultir;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

public class RelatorioNotificGermeMultirresistenteController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6859107032113993399L;
	
	private AghUnidadesFuncionais unidadeFuncional;	
	
	private MciBacteriaMultir bacteriaMultir;

	
	private String valorParametro;

	private Collection<RelatorioNotificGermeMultirresistenteVO> lista;
	
	private DominioSituacao situacao;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IAghuFacade aghuFacade;
	@EJB
	private ICadastrosBasicosInternacaoFacade  cadastrosBasicosInternacaoFacade;
	
	private static final String RELATORIO_NOTIFIC_GERME_MULTIRRESISTENTE_PDF = "relatorioNotificGermeMultirresistentePdf";
	private static final String RELATORIO_NOTIFIC_GERME_MULTIRRESISTENTE = "relatorioNotificGermeMultirresistente";
	
	@PostConstruct
	public void inicio() {
		begin(conversation);
	}
	
	public List<AghUnidadesFuncionais> listarAghUnidadesFuncionais(Object parametro) {
		return aghuFacade.listarAghUnidadesFuncionais(parametro);
	}
	
	
	public String visualizarRelatorio(){
		return RELATORIO_NOTIFIC_GERME_MULTIRRESISTENTE_PDF;
	}
	
	public String voltar(){
		return RELATORIO_NOTIFIC_GERME_MULTIRRESISTENTE;
	}
	
	private void obterValorParametro(){
		try {
			AghParametros parametros;
			parametros = parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_CIDS_FIBROSE_CISTICA);
			valorParametro = parametros.getVlrTexto();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}
	
	// Suggestion Bacteria Multirresistente
	public 	List<MciBacteriaMultir> listarSuggestionBoxBacteriaMultir(String filtro){
		return returnSGWithCount(controleInfeccaoFacade.listarSuggestionBoxBacteriaMultir(filtro), controleInfeccaoFacade.listarSuggestionBoxBacteriaMultirCount(filtro));
	}
	
	// Suggestion Bacteria Unidade Funcional
	public 	List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String filtro){
		return returnSGWithCount(aghuFacade.pesquisarAghUnidadesFuncionaisPorCodigoDescricao(filtro, true) , aghuFacade.pesquisarAghUnidadesFuncionaisPorSequencialOuDescricaoCount(filtro));
	}
	@Override
	protected Collection<RelatorioNotificGermeMultirresistenteVO> recuperarColecao() throws ApplicationBusinessException {
		obterValorParametro();
		lista = controleInfeccaoFacade.obterDadosRelatorioGermesMultirresistente(getValorParametro(), getBacteriaMultir() != null ? getBacteriaMultir().getSeq() : null , getUnidadeFuncional() != null ? getUnidadeFuncional().getSeq() : null, getSituacao() == null? null : getSituacao().isAtivo());
		return lista;
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
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dtAtual", sdf.format(dataAtual));
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);

		return params;
	}
	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/controleinfeccao/report/relatorioNotificGermeMultirresistente.jasper";
	}
	
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public MciBacteriaMultir getBacteriaMultir() {
		return bacteriaMultir;
	}

	public void setBacteriaMultir(MciBacteriaMultir bacteriaMultir) {
		this.bacteriaMultir = bacteriaMultir;
	}

	public Collection<RelatorioNotificGermeMultirresistenteVO> getLista() {
		return lista;
	}

	public void setLista(Collection<RelatorioNotificGermeMultirresistenteVO> lista) {
		this.lista = lista;
	}

	public String getValorParametro() {
		return valorParametro;
	}

	public void setValorParametro(String valorParametro) {
		this.valorParametro = valorParametro;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

}
