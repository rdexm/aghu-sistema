package br.gov.mec.aghu.compras.pac.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;

/**
 * Critério para Consulta de Licitações a Liberar
 * 
 * @author mlcruz
 */
public class LicitacoesLiberarCriteriaVO implements Serializable {
	private static final long serialVersionUID = -3433243684781379361L;
	
	/** Número do PAC */
	private Integer numero;
	
	/** Descrição do PAC */
	private String descricao;
	
	/** Período de Geração */
	private Periodo geracao = new Periodo();
	
	/** Tipo de Solicitação */
	private DominioTipoSolicitacao tipo = DominioTipoSolicitacao.SC;
	
	/** Número da SC */
	private Integer solicitacaoCompraId;
	
	/** Material */
	private ScoMaterial material;
	
	/** Número da SS */
	private Integer solicitacaoServicoId;
	
	/** Serviço */
	private ScoServico servico;
	
	/** Gestor */
	private RapServidores gestor;
	
	/** Modalidade */
	private ScoModalidadeLicitacao modalidade;
	
	// Getters/Setters

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Periodo getGeracao() {
		return geracao;
	}

	public void setGeracao(Periodo geracao) {
		this.geracao = geracao;
	}

	public DominioTipoSolicitacao getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoSolicitacao tipo) {
		this.tipo = tipo;
	}

	public Integer getSolicitacaoCompraId() {
		return solicitacaoCompraId;
	}

	public void setSolicitacaoCompraId(Integer solicitacaoCompraId) {
		this.solicitacaoCompraId = solicitacaoCompraId;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public Integer getSolicitacaoServicoId() {
		return solicitacaoServicoId;
	}

	public void setSolicitacaoServicoId(Integer solicitacaoServicoId) {
		this.solicitacaoServicoId = solicitacaoServicoId;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public RapServidores getGestor() {
		return gestor;
	}

	public void setGestor(RapServidores gestor) {
		this.gestor = gestor;
	}

	public ScoModalidadeLicitacao getModalidade() {
		return modalidade;
	}

	public void setModalidade(ScoModalidadeLicitacao modalidade) {
		this.modalidade = modalidade;
	}
	
	/**
	 * Indica se algum filtro foi informado.
	 * 
	 * @return Flag
	 */
	public Boolean isEmpty() {
		if (numero == null && descricao == null) {
			if (geracao.inicio == null && geracao.fim == null) {
				if (tipo == null){
					if (gestor == null && modalidade == null) {
						return true;
					}
				}
				else {
					switch (tipo) {
					case SC:
						if (solicitacaoCompraId != null || material != null) {
							return false;
						}
						
						break;
						
					case SS:
						if (solicitacaoServicoId != null || servico != null) {
							return false;
						}
						
						break;
					}
					
					if (gestor == null && modalidade == null) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	/**
	 * Período
	 */
	public class Periodo {
		/** Inicio */
		private Date inicio;
		
		/** Fim */
		private Date fim;
		
		// Getters/Setters

		public Date getInicio() {
			return inicio;
		}

		public void setInicio(Date inicio) {
			this.inicio = inicio;
		}

		public Date getFim() {
			return fim;
		}

		public void setFim(Date fim) {
			this.fim = fim;
		}
	}
}