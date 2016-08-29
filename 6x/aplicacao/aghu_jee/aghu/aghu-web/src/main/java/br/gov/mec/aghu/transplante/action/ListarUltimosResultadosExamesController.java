package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MtxExameUltResults;
import br.gov.mec.aghu.model.MtxResultadoExames;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.ListarTransplantesVO;
import br.gov.mec.aghu.transplante.vo.PacienteAguardandoTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.TiposExamesPacienteVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ListarUltimosResultadosExamesController extends ActionController implements ActionPaginator {
	
	/**
	 * #47146
	 */
	private static final long serialVersionUID = -6821022292579568944L;
	
	private static final String REDIRECT_LISTAR_TRANSPLANTES_ORGAO = "transplante-listarTransplanteOrgao";

	@Inject @Paginator
	private DynamicDataModel<ListarTransplantesVO> dataModel4;
	
	private Boolean mostrarColuna;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	@EJB
	private ITransplanteFacade transplanteFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	private ListarTransplantesVO filtro; 
	private ListarTransplantesVO itemSelecionado; 
	private PacienteAguardandoTransplanteOrgaoVO itemSelecionadoExames; 
	private MtxTransplantes transplante;
	private String resultadoPcr;
	private Date dataPcr;
	private MtxResultadoExames resultadoExames;
	private MtxExameUltResults mtxExameUltResults;
	private Boolean flagEditar = false;
	private List<MtxResultadoExames> resultadoExamesList;
	private List<TiposExamesPacienteVO> listaVoC3 = new ArrayList<TiposExamesPacienteVO>();
	private AipPacientes paciente;
	private RapServidores servidorLogado;
	private Boolean flagExibirPainel;
	private Integer seqTransplante;
	private String resultado;
	
	@PostConstruct
	protected void inicializar() {
		begin(conversation);
	}
	
	public void inicio() {
		limpar();
		servidorLogado = servidorLogadoFacade.obterServidorLogado();
		// Executa C1
		paciente = transplanteFacade.obterDadosPaciente(seqTransplante);
		
		// Executa C2
		// Executa C3 para cada item da lista de C2
		listaVoC3 = transplanteFacade.buscaUltimosResultados(paciente);
		
		try {
			//Executa C4 e caso exista reagente, retorna true para renderização do servedatatable
			flagExibirPainel = transplanteFacade.verificarHcvReagente(seqTransplante, paciente, mtxExameUltResults);

			if (flagExibirPainel) {
				//Executa C5
				resultadoExamesList = transplanteFacade.obterResultadoExames(seqTransplante, paciente);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void adicionar(){
		
		
		try {
			
			if((resultado != null  && dataPcr != null)){
				
				this.resultadoExames = new MtxResultadoExames();
				resultadoExames.setResultado(resultado);
				resultadoExames.setData(dataPcr);
				resultadoExames.setServidor(servidorLogado);
				resultadoExames.setCriadoEm(new Date());
				
				transplanteFacade.salvarResultadoExames(resultadoExames, seqTransplante);
					
				resultadoExamesList = transplanteFacade.obterResultadoExames(seqTransplante, paciente);
				apresentarMsgNegocio(Severity.INFO, "MSG_GRAVAR_SUCESSO_RESULTADO_EXAMES");
				limpar();
				resultadoExames = null;
			}
		}catch (Exception e){
			apresentarMsgNegocio(Severity.FATAL, "MSG_ERRO_ADICIONAR_RESULTADO_EXAMES", new Object[] {e.getMessage()});
		}
	}
	
	public void editar(){
		limparEdicao();
		flagEditar = true;
		
		resultado = resultadoExames.getResultado();
		dataPcr = resultadoExames.getData();
		
		resultadoExames.setEdita(true);
		
	}
	
	public String excluir(){
		limparEdicao();
		if((resultadoExames != null && resultadoExames.getSeq() != null)){
			try{
				transplanteFacade.excluirResultadoExames(resultadoExames);
				resultadoExamesList = transplanteFacade.obterResultadoExames(seqTransplante, paciente);
				apresentarMsgNegocio(Severity.INFO, "MSG_EXCLUIR_RESULTADO_EXAMES");
			}catch (Exception e){
				apresentarMsgNegocio(Severity.FATAL, "MSG_ERRO_EXCLUIR_RESULTADO_EXAMES", new Object[] {e.getMessage()});
			}
		}
		return null;
	}
	
	public void limpar(){
		resultado = null;
		dataPcr = null;
	}
	
	public void limparEdicao(){
		
		for (MtxResultadoExames listarEdicao : resultadoExamesList) {
			listarEdicao.setEdita(false);	
		}

	}
	
	public void cancelarEdicao(){
		flagEditar = false;
		limparEdicao();
		limpar();
		resultadoExames = null;
	}
	
	public void confirmarEdicao(){
		flagEditar = false;
		
		resultadoExames.setResultado(resultado);
		resultadoExames.setData(dataPcr);
		transplanteFacade.atualizarResultadoExames(resultadoExames);
		
		limparEdicao();
		limpar();
		resultadoExames = null;
		apresentarMsgNegocio(Severity.INFO, "MSG_EDICAO_SUCESSO_RESULTADO_EXAMES");	
	}
	
	public String cancelar(){
		return REDIRECT_LISTAR_TRANSPLANTES_ORGAO;
	}
	
	@Override
	public Long recuperarCount() {
		return transplanteFacade.obterPacientesTransplantadosPorFiltroCount(filtro);
	}

	@Override
	public List<ListarTransplantesVO> recuperarListaPaginada(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		try{
			return transplanteFacade.obterPacientesTransplantadosPorFiltro(filtro, firstResult, maxResults, orderProperty, asc);
		}catch(ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String obterProntuarioFormatado(String prontuario){
		if (StringUtils.isEmpty(prontuario)) {
			return StringUtils.EMPTY;
		}
		return CoreUtil.formataProntuario(prontuario).replace("/", "-");
	}
	
	public String obterPermanenciaPaciente(ListarTransplantesVO itemSelecionado){
		return DateUtil.calcularDiasEntreDatas(itemSelecionado.getDataSituacao(), itemSelecionado.getDataSituacaoAtual()) + " dias";
	}
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
    }

	public DynamicDataModel<ListarTransplantesVO> getDataModel4() {
		return dataModel4;
	}

	public void setDataModel4(DynamicDataModel<ListarTransplantesVO> dataModel4) {
		this.dataModel4 = dataModel4;
	}

	public Boolean getMostrarColuna() {
		return mostrarColuna;
	}

	public void setMostrarColuna(Boolean mostrarColuna) {
		this.mostrarColuna = mostrarColuna;
	}

	public ITransplanteFacade getTransplanteFacade() {
		return transplanteFacade;
	}

	public void setTransplanteFacade(ITransplanteFacade transplanteFacade) {
		this.transplanteFacade = transplanteFacade;
	}

	public ListarTransplantesVO getFiltro() {
		return filtro;
	}

	public void setFiltro(ListarTransplantesVO filtro) {
		this.filtro = filtro;
	}

	public ListarTransplantesVO getItemSelecionado4() {
		return itemSelecionado;
	}

	public void setItemSelecionado4(ListarTransplantesVO itemSelecionado4) {
		this.itemSelecionado = itemSelecionado4;
	}

	public PacienteAguardandoTransplanteOrgaoVO getItemSelecionadoExames() {
		return itemSelecionadoExames;
	}

	public void setItemSelecionadoExames(
			PacienteAguardandoTransplanteOrgaoVO itemSelecionadoExames) {
		this.itemSelecionadoExames = itemSelecionadoExames;
	}

	public String getResultadoPcr() {
		return resultadoPcr;
	}

	public void setResultadoPcr(String resultadoPcr) {
		this.resultadoPcr = resultadoPcr;
	}

	public Date getDataPcr() {
		return dataPcr;
	}

	public void setDataPcr(Date dataPcr) {
		this.dataPcr = dataPcr;
	}

	public MtxResultadoExames getResultadoExames() {
		return resultadoExames;
	}

	public void setResultadoExames(MtxResultadoExames resultadoExames) {
		this.resultadoExames = resultadoExames;
	}

	public Boolean getFlagEditar() {
		return flagEditar;
	}

	public void setFlagEditar(Boolean flagEditar) {
		this.flagEditar = flagEditar;
	}

	public List<MtxResultadoExames> getResultadoExamesList() {
		return resultadoExamesList;
	}

	public void setResultadoExamesList(List<MtxResultadoExames> resultadoExamesList) {
		this.resultadoExamesList = resultadoExamesList;
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public ListarTransplantesVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ListarTransplantesVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public MtxTransplantes getTransplante() {
		return transplante;
	}

	public void setTransplante(MtxTransplantes transplante) {
		this.transplante = transplante;
	}

	public List<TiposExamesPacienteVO> getListaVoC3() {
		return listaVoC3;
	}

	public void setListaVoC3(List<TiposExamesPacienteVO> listaVoC3) {
		this.listaVoC3 = listaVoC3;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public Boolean getFlagExibirPainel() {
		return flagExibirPainel;
	}

	public void setFlagExibirPainel(Boolean flagExibirPainel) {
		this.flagExibirPainel = flagExibirPainel;
	}

	public Integer getSeqTransplante() {
		return seqTransplante;
	}

	public void setSeqTransplante(Integer seqTransplante) {
		this.seqTransplante = seqTransplante;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	
}