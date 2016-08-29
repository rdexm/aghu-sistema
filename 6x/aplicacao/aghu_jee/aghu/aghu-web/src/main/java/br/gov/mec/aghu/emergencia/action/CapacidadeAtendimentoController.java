package br.gov.mec.aghu.emergencia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.MamCapacidadeAtendVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * @author israel.haas
 */

public class CapacidadeAtendimentoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3765844896472728206L;
	
	private static final String REDIRECIONA_LISTAR_CAPACIDADE_ATEND = "capacidadeAtendimentoList";

	@Inject
	private IEmergenciaFacade emergenciaFacade;
	
	@Inject
	private CapacidadeAtendimentoPaginatorController capacidadeAtendimentoPaginatorController;
	
	private Integer capacidadeSeq;
	private Short qtdeInicialPac;
	private Short qtdeFinalPac;
	private Short capacidadeAtend;
	private Boolean indSituacao;
	private Short espSeq;
	private Especialidade especialidade;
	private MamCapacidadeAtendVO mamCapacidadeAtendVO;
	
	private Boolean bloquearAgenda;
	
	private List<Short> listaEspId;

	@PostConstruct
	public void init() {
		begin(conversation);	
	}
	
	public void inicio() {
		if (this.capacidadeSeq != null) {
			try {
				setEspecialidade(this.emergenciaFacade.obterEspecialidadePorSeq(this.espSeq));
				this.carregaValorParaEdicao();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			setBloquearAgenda(Boolean.TRUE);
		} else {
			setBloquearAgenda(Boolean.FALSE);
		}
	}
	
	public List<Especialidade> pesquisarEspecialidadeListaSeq(String objPesquisa) {
		setListaEspId(this.emergenciaFacade.pesquisarSeqsEspecialidadesEmergencia(null, DominioSituacao.A));
		
		return this.emergenciaFacade.pesquisarEspecialidadeListaSeq(getListaEspId(), objPesquisa);				
	}

	public String confirmar() {
		capacidadeAtendimentoPaginatorController.reiniciarPaginator();
		
		try {
			boolean create = this.capacidadeSeq == null;
			
			this.emergenciaFacade.persistirCapacidadeAtend(this.especialidade.getSeq(), this.capacidadeSeq,
					this.qtdeInicialPac, this.qtdeFinalPac, this.capacidadeAtend, this.indSituacao);
			
			this.emergenciaFacade.atualizaPrevisaoAtendimento(this.especialidade.getSeq());
			
			if (create) {
				apresentarMsgNegocio(Severity.INFO,
						"INCLUSAO_CAPACIDADE_ATEND_SUCESSO");
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"ALTERACAO_CAPACIDADE_ATEND_SUCESSO");
			}
			limpar();
			
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);

			return null;
		}
		return REDIRECIONA_LISTAR_CAPACIDADE_ATEND;
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * Nacionalidades
	 */
	public String cancelar() {
		limpar();
		return REDIRECIONA_LISTAR_CAPACIDADE_ATEND;
	}
	
	private void limpar() {
		setCapacidadeSeq(null);
		setQtdeInicialPac(null);
		setQtdeFinalPac(null);
		setCapacidadeAtend(null);
		setIndSituacao(Boolean.TRUE);
		setEspecialidade(null);
		setBloquearAgenda(Boolean.FALSE);
	}
	
	private void carregaValorParaEdicao() {
		if (getMamCapacidadeAtendVO() != null) {
			setQtdeFinalPac(getMamCapacidadeAtendVO().getQtdeFinalPac());
			setQtdeInicialPac(getMamCapacidadeAtendVO().getQtdeInicialPac());
			setCapacidadeAtend(getMamCapacidadeAtendVO().getCapacidadeAtend());
			setIndSituacao(getMamCapacidadeAtendVO().getIndSituacao() != null ? getMamCapacidadeAtendVO().getIndSituacao().isAtivo() : Boolean.FALSE);
		} else {
			setIndSituacao(Boolean.TRUE);
		}
	}
	
	// ### GETs e SETs ###
	public Integer getCapacidadeSeq() {
		return capacidadeSeq;
	}

	public void setCapacidadeSeq(Integer capacidadeSeq) {
		this.capacidadeSeq = capacidadeSeq;
	}

	public Short getQtdeInicialPac() {
		return qtdeInicialPac;
	}

	public void setQtdeInicialPac(Short qtdeInicialPac) {
		this.qtdeInicialPac = qtdeInicialPac;
	}

	public Short getQtdeFinalPac() {
		return qtdeFinalPac;
	}

	public void setQtdeFinalPac(Short qtdeFinalPac) {
		this.qtdeFinalPac = qtdeFinalPac;
	}

	public Short getCapacidadeAtend() {
		return capacidadeAtend;
	}

	public void setCapacidadeAtend(Short capacidadeAtend) {
		this.capacidadeAtend = capacidadeAtend;
	}

	public Boolean getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}

	public Boolean getBloquearAgenda() {
		return bloquearAgenda;
	}

	public void setBloquearAgenda(Boolean bloquearAgenda) {
		this.bloquearAgenda = bloquearAgenda;
	}

	public List<Short> getListaEspId() {
		return listaEspId;
	}

	public void setListaEspId(List<Short> listaEspId) {
		this.listaEspId = listaEspId;
	}

	public MamCapacidadeAtendVO getMamCapacidadeAtendVO() {
		return mamCapacidadeAtendVO;
	}

	public void setMamCapacidadeAtendVO(MamCapacidadeAtendVO mamCapacidadeAtendVO) {
		this.mamCapacidadeAtendVO = mamCapacidadeAtendVO;
	}
}