package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.TransferirExamesVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;

public class TransferirExamesController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6050807237440854951L;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	private Integer numero;
	
	private Integer cacheNumero;
	
	private TransferirExamesVO deConsulta = new TransferirExamesVO();
	
	private Integer numeroFiltro;
	
	private List<TransferirExamesVO> listaSolicitacoesExames;
	
	private List<TransferirExamesVO> listaParaConsultas;
	
	private TransferirExamesVO paraConsultaSelecionada;
	
	private Boolean solitacoesExames = false;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Consultar De Consultas
	 */
	public void pesquisarDeConsulta() {
		
		if (this.numero != null) {
			if (this.cacheNumero == null || !this.cacheNumero.equals(this.numero)) {
				List<TransferirExamesVO> listaDeConsulta = ambulatorioFacade.obterDeConsulta(numero);
				this.deConsulta = new TransferirExamesVO();
				if (listaDeConsulta != null && !listaDeConsulta.isEmpty()) {
					this.deConsulta = listaDeConsulta.get(0);
				}
				this.pesquisarSolicitacoesExames();
				this.limpar();
				this.cacheNumero = this.numero;
				
			}
		}
	}
	
	/**
	 * Consultar Solicitações de Exames
	 */
	private void pesquisarSolicitacoesExames() {
		if (this.deConsulta != null && this.deConsulta.getSeq() != null) {
			this.listaSolicitacoesExames = ambulatorioFacade.obterSolicitacoesExames(this.deConsulta.getSeq());
			if (listaSolicitacoesExames != null && !listaSolicitacoesExames.isEmpty()) {
				this.solitacoesExames = true;
			}else{
				this.solitacoesExames = false;
			}
		}else{
			this.listaSolicitacoesExames = new ArrayList<TransferirExamesVO>();
			this.solitacoesExames = false;
		}
	}
	
	/**
	 * Consultar Para Consultas
	 */
	public void pesquisarParaConsultas() {
		this.listaParaConsultas = new ArrayList<TransferirExamesVO>();
		this.paraConsultaSelecionada = null;
		this.listaParaConsultas = ambulatorioFacade.obterParaConsultas(numeroFiltro, deConsulta.getCodigoPaciente());
	}
	
	/**
	 * Limpar os campos das tela e remove a grid
	 */
	public void limpar() {
		this.numeroFiltro = null;
		this.listaParaConsultas = null;
		this.paraConsultaSelecionada = null;
	}
	
	public void trocarSolicitacoes() {
		if (this.deConsulta != null && this.deConsulta.getSeq() != null
			&& this.paraConsultaSelecionada != null && this.paraConsultaSelecionada.getSeq() != null) {
			Integer qdtSolicitacoes = 0;
			qdtSolicitacoes = this.ambulatorioFacade.trocarSolicitacoes(this.deConsulta.getSeq(), this.paraConsultaSelecionada.getSeq());
			if (qdtSolicitacoes != null && qdtSolicitacoes > 0) {
				if (qdtSolicitacoes == 1) {
					this.apresentarMsgNegocio(Severity.INFO, "TRANSFERIR_EXAME_SUCESSO");
				}
				else{
					this.apresentarMsgNegocio(Severity.INFO, "TRANSFERIR_EXAMES_SUCESSO", qdtSolicitacoes);
				}
				cacheNumero = null;
				pesquisarDeConsulta();
				return;
			}
		}
		this.apresentarMsgNegocio(Severity.INFO, "TRANSFERIR_EXAMES_ERRO");
	}

	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public TransferirExamesVO getDeConsulta() {
		return deConsulta;
	}
	public void setDeConsulta(TransferirExamesVO deConsulta) {
		this.deConsulta = deConsulta;
	}
	public Integer getNumeroFiltro() {
		return numeroFiltro;
	}
	public void setNumeroFiltro(Integer numeroFiltro) {
		this.numeroFiltro = numeroFiltro;
	}
	public List<TransferirExamesVO> getListaSolicitacoesExames() {
		return listaSolicitacoesExames;
	}
	public void setListaSolicitacoesExames(
			List<TransferirExamesVO> listaSolicitacoesExames) {
		this.listaSolicitacoesExames = listaSolicitacoesExames;
	}
	public List<TransferirExamesVO> getListaParaConsultas() {
		return listaParaConsultas;
	}
	public void setListaParaConsultas(List<TransferirExamesVO> listaParaConsultas) {
		this.listaParaConsultas = listaParaConsultas;
	}
	public TransferirExamesVO getParaConsultaSelecionada() {
		return paraConsultaSelecionada;
	}
	public void setParaConsultaSelecionada(
			TransferirExamesVO paraConsultaSelecionada) {
		this.paraConsultaSelecionada = paraConsultaSelecionada;
	}

	public Boolean getSolitacoesExames() {
		return solitacoesExames;
	}

	public void setSolitacoesExames(Boolean solitacoesExames) {
		this.solitacoesExames = solitacoesExames;
	}
}
