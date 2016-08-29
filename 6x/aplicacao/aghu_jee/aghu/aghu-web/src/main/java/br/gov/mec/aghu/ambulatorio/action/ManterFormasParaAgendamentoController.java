package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndAtendimentoAnterior;
import br.gov.mec.aghu.dominio.DominioIndExameRH;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacFormaAgendamentoId;
import br.gov.mec.aghu.model.AacFormaEspecialidade;
import br.gov.mec.aghu.model.AacFormaEspecialidadeId;
import br.gov.mec.aghu.model.AacNivelBusca;
import br.gov.mec.aghu.model.AacNivelBuscaId;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterFormasParaAgendamentoController extends ActionController {

	private static final long serialVersionUID = 7309648815506283420L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private AacFormaAgendamento formaAgendamento;

	private Short caaSeq;

	private Short tagSeq;

	private Short pgdSeq;

	private boolean edicao;

	private boolean edicaoNivelBusca;

	private boolean edicaoFormaEspecialidade;

	private List<AacNivelBusca> niveisBuscas;

	private List<AacFormaEspecialidade> formasEspecialidades;

	private AacNivelBusca nivelBusca;

	private AacNivelBuscaId idNivelBusca;

	private AacFormaEspecialidade formaEspecialidade;

	private AacFormaEspecialidadeId idFormaEspecialidade;

	private Short espSeq;

	private Short seqp;

	private final String PAGE_FORMAS_AGENDAMENTO_LIST = "manterFormasParaAgendamentoList";


	public void inicio() {
	 

	 

		
		this.edicaoNivelBusca = false;
		this.edicaoFormaEspecialidade = false;

		this.nivelBusca = new AacNivelBusca();
		this.formaEspecialidade = new AacFormaEspecialidade();
		this.idNivelBusca = null;
		this.idFormaEspecialidade = null;
		this.espSeq = null;
		this.seqp = null;

		if (this.caaSeq != null && this.tagSeq != null && this.pgdSeq != null) {
			final Enum[] innerJoins = {AacFormaAgendamento.Fields.CONDICAO_ATENDIMENTO, 
									   AacFormaAgendamento.Fields.TIPO_AGENDAMENTO, 
									   AacFormaAgendamento.Fields.PAGADOR};
			
			this.formaAgendamento = this.ambulatorioFacade.obterAacFormaAgendamentoPorChavePrimaria(
										new AacFormaAgendamentoId(this.caaSeq, this.tagSeq, this.pgdSeq), 
										innerJoins, 
										null);
		}

		if (this.formaAgendamento == null) {
			this.formaAgendamento = new AacFormaAgendamento();
			this.niveisBuscas = new ArrayList<AacNivelBusca>(0);
			this.formasEspecialidades = new ArrayList<AacFormaEspecialidade>(0);
			
			// Valores default para novo registro
			this.formaAgendamento.setAtendimentoAnterior(DominioIndAtendimentoAnterior.C);
			this.formaAgendamento.setExameRh(DominioIndExameRH.N);
			this.formaAgendamento.setExigeProntuario(true); 
			this.formaAgendamento.setPermiteExtra(true);
			this.formaAgendamento.setSenhaAutoriza(true);
			this.formaAgendamento.setExigeGrdFamiliar(false);
			this.formaAgendamento.setExigeSmo(true);
			this.formaAgendamento.setMesmaGrade(true);

			this.edicao = false;
		} else {
			this.edicao = true;

			if (this.formaAgendamento.getNiveisBusca() != null) {
				this.niveisBuscas = this.ambulatorioFacade.pesquisarNivelBuscaPorFormaAgendamento(this.formaAgendamento);
			} else {
				this.niveisBuscas = new ArrayList<AacNivelBusca>(0);
			}

			if (this.formaAgendamento.getFormaEspecialidades() != null) {
				this.formasEspecialidades = this.ambulatorioFacade.listaFormasEspecialidadePorFormaAgendaneto(this.formaAgendamento);
			} else {
				this.formasEspecialidades = new ArrayList<AacFormaEspecialidade>(0);
			}
		}
	
	}
	

	public void salvarFormaAgendamento() {
		try {
			if (this.caaSeq == null && this.tagSeq == null && this.pgdSeq == null) {
				if (formaAgendamento.getPagador() == null) {
					apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Órgão Pagador");
					return;
				}
				
				if (formaAgendamento.getTipoAgendamento() == null) {
					apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Tipo de Autorização");
					return;
				}

				if (formaAgendamento.getCondicaoAtendimento() == null) {
					apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Condição Atendimento");
					return;
				}
				
				this.ambulatorioFacade.salvarFormaAgendamento(this.formaAgendamento);

				this.caaSeq = this.formaAgendamento.getId().getCaaSeq();
				this.tagSeq = this.formaAgendamento.getId().getTagSeq();
				this.pgdSeq = this.formaAgendamento.getId().getPgdSeq();

				this.inicio();

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_FORMA_AGENDAMENTO");
				
				if ((DominioIndAtendimentoAnterior.A.equals(this.formaAgendamento.getAtendimentoAnterior()) || DominioIndAtendimentoAnterior.C
						.equals(this.formaAgendamento.getAtendimentoAnterior()))
						&& (niveisBuscas == null || niveisBuscas.isEmpty())) {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_INFORME_NIVEL_BUSCA");
				}
			} else {
				this.ambulatorioFacade.alterarFormaAgendamento(this.formaAgendamento);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_FORMA_AGENDAMENTO");
				
				if (DominioIndAtendimentoAnterior.A.equals(this.formaAgendamento.getAtendimentoAnterior()) || 
						DominioIndAtendimentoAnterior.C.equals(this.formaAgendamento.getAtendimentoAnterior())){

					final Long result = ambulatorioFacade.obterQuantidadeAacNivelBuscaPorFormaAgendamento(formaAgendamento);
					
					if(Long.valueOf(0).equals(result)){
						apresentarMsgNegocio(Severity.INFO, "MENSAGEM_INFORME_NIVEL_BUSCA");
					}
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void editarNivelBusca(AacNivelBuscaId id) {
		this.edicaoNivelBusca = true;
		this.idNivelBusca = id;
		Enum[] innerJoins = {AacNivelBusca.Fields.CONDICAO_ATENDIMENTO, AacNivelBusca.Fields.TIPO_AGENDAMENTO, AacNivelBusca.Fields.PAGADOR};
		Enum[] leftJoins = {AacNivelBusca.Fields.CLINICA};
		this.nivelBusca = this.ambulatorioFacade.obterAacNivelBuscaPorChavePrimaria(id, innerJoins, leftJoins);
	}

	public void salvarNivelBusca() {
		try {
			Short fagCaaSeq = this.formaAgendamento.getId().getCaaSeq();
			Short fagTagSeq = this.formaAgendamento.getId().getTagSeq();
			Short fagPgdSeq = this.formaAgendamento.getId().getPgdSeq();
			Short seqp = this.ambulatorioFacade.buscaProximoSeqpAacNivelBusca(fagCaaSeq, fagTagSeq, fagPgdSeq);

			this.nivelBusca.setId(new AacNivelBuscaId(fagCaaSeq, fagTagSeq, fagPgdSeq, seqp));
			
			this.ambulatorioFacade.salvarNivelBusca(this.nivelBusca);
	
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_ATENDIMENTO_ANTERIOR");

			// Limpa o fieldset de níveis busca
			this.limparEdicaoNivelBusca();

			// Recarrega as informações da tela
			this.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CRIACAO_ATENDIMENTO_ANTERIOR");
		}
	}

	public void alterarNivelBusca() {
		try {
			this.ambulatorioFacade.alterarNivelBusca(this.nivelBusca);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_ATENDIMENTO_ANTERIOR");

			// Limpa o fieldset de níveis busca
			this.limparEdicaoNivelBusca();

			// Recarrega as informações da tela
			this.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EDICAO_ATENDIMENTO_ANTERIOR");
		}
	}

	public void excluirNivelBusca() {
		this.ambulatorioFacade.removerNivelBusca(new AacNivelBuscaId(formaAgendamento.getId().getCaaSeq(), 
																	 formaAgendamento.getId().getTagSeq(), 
																	 formaAgendamento.getId().getPgdSeq(), 
																	 this.seqp));

		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ATENDIMENTO_ANTERIOR");

		// Recarrega as informações da tela
		this.recarregarInformacoesTela();
	}

	public void limparEdicaoNivelBusca() {
		this.edicaoNivelBusca = false;
		this.idNivelBusca = null;
		this.nivelBusca = new AacNivelBusca();
	}

	public void editarFormaEspecialidade(AacFormaEspecialidadeId id) {
		this.edicaoFormaEspecialidade = true;
		this.idFormaEspecialidade = id;
		this.formaEspecialidade = this.ambulatorioFacade.obterAacFormaEspecialidadePorChavePrimaria(id, new Enum[] {AacFormaEspecialidade.Fields.ESPECIALIDADE}, null);
	}

	public void salvarFormaEspecialidade() {
		try {
			if (this.formaEspecialidade.getEspecialidade() == null) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_SELECIONE_ESPECIALIDADE_PARA_PERSISTIR");
			} else {
				Short fagCaaSeq = this.formaAgendamento.getId().getCaaSeq();
				Short fagTagSeq = this.formaAgendamento.getId().getTagSeq();
				Short fagPgdSeq = this.formaAgendamento.getId().getPgdSeq();
				Short espSeq = this.formaEspecialidade.getEspecialidade().getSeq();
				
				this.formaEspecialidade.setId(new AacFormaEspecialidadeId(fagCaaSeq, fagTagSeq, fagPgdSeq, espSeq));
				
				this.ambulatorioFacade.salvarFormaEspecialidade(this.formaEspecialidade);
	
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_FORMA_ESPECIALIDADE");
	
				// Limpa o fieldset de formas especialidades
				this.limparEdicaoFormaEspecialidade();
	
				// Recarrega as informações da tela
				this.recarregarInformacoesTela();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CRIACAO_FORMA_ESPECIALIDADE");
		}
	}

	public void alterarFormaEspecialidade() {
		try {
			this.ambulatorioFacade.alterarFormaEspecialidade(this.formaEspecialidade);

			// Limpa o fieldset de forma especialidade
			this.limparEdicaoFormaEspecialidade();

			// Recarrega as informações da tela
			this.recarregarInformacoesTela();
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_FORMA_ESPECIALIDADE");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EDICAO_FORMA_ESPECIALIDADE");
		}
	}

	public void excluirFormaEspecialidade() {
		try {
			this.ambulatorioFacade.removerFormaEspecialidade(new AacFormaEspecialidadeId(formaAgendamento.getId().getCaaSeq(), 
																						 formaAgendamento.getId().getTagSeq(), 
																						 formaAgendamento.getId().getPgdSeq(), 
																						 this.espSeq
																						 )
															);
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_FORMA_ESPECIALIDADE");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		// Recarrega as informações da tela
		this.recarregarInformacoesTela();
	}

	public void cancelarEdicaoFormaEspecialidade(){
		this.limparEdicaoFormaEspecialidade();
	}
	
	public void limparEdicaoFormaEspecialidade() {
		this.edicaoFormaEspecialidade = false;
		this.idFormaEspecialidade = null;
		this.formaEspecialidade = new AacFormaEspecialidade();
	}
	
	public void recarregarInformacoesTela() {
		this.inicio();
	}

	public Long pesquisarPagadoresCount(String filtro) {
		return this.ambulatorioFacade.pesquisarPagadoresCount((String) filtro);
	}

	public List<AacPagador> pesquisarPagadores(String filtro) {
		return this.returnSGWithCount(this.ambulatorioFacade.pesquisarPagadores((String) filtro),pesquisarPagadoresCount(filtro));
	}

	public Long pesquisarTiposAgendamentoCount(String filtro) {
		return this.ambulatorioFacade.pesquisarTiposAgendamentoCount((String) filtro);
	}

	public List<AacTipoAgendamento> pesquisarTiposAgendamento(String filtro) {
		return this.returnSGWithCount(this.ambulatorioFacade.pesquisarTiposAgendamento((String) filtro),pesquisarTiposAgendamentoCount(filtro));
	}

	public Long pesquisarCondicoesAtendimentoCount(String filtro) {
		return this.ambulatorioFacade.pesquisarCondicoesAtendimentoCount((String) filtro);
	}

	public List<AacCondicaoAtendimento> pesquisarCondicoesAtendimento(String filtro) {
		return this.returnSGWithCount(this.ambulatorioFacade.pesquisarCondicoesAtendimento((String) filtro),pesquisarCondicoesAtendimentoCount(filtro));
	}

	public Long pesquisarClinicasCount(String filtro) {
		return this.aghuFacade.pesquisarClinicasCount((String) filtro);
	}

	public List<AghClinicas> pesquisarClinicas(String filtro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarClinicas((String) filtro),pesquisarClinicasCount(filtro));
	}

	public Long pesquisarEspecialidadesAgendasCount(String filtro) {
		return this.aghuFacade.pesquisarEspecialidadesAgendasCount((String) filtro);
	}

	public List<AghEspecialidades> pesquisarEspecialidadesAgendas(String filtro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidadesAgendas((String) filtro),pesquisarEspecialidadesAgendasCount(filtro));
	}

	public String cancelar() {
		this.formaAgendamento = null;
		this.edicao = false;
		this.caaSeq = null;
		this.tagSeq = null;
		this.pgdSeq = null;
		formaAgendamento = null;
		cancelarEdicaoFormaEspecialidade();
		return PAGE_FORMAS_AGENDAMENTO_LIST;
	}

	public AacFormaAgendamento getFormaAgendamento() {
		return formaAgendamento;
	}

	public void setFormaAgendamento(AacFormaAgendamento formaAgendamento) {
		this.formaAgendamento = formaAgendamento;
	}

	public Short getCaaSeq() {
		return caaSeq;
	}

	public void setCaaSeq(Short caaSeq) {
		this.caaSeq = caaSeq;
	}

	public Short getTagSeq() {
		return tagSeq;
	}

	public void setTagSeq(Short tagSeq) {
		this.tagSeq = tagSeq;
	}

	public Short getPgdSeq() {
		return pgdSeq;
	}

	public void setPgdSeq(Short pgdSeq) {
		this.pgdSeq = pgdSeq;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public List<AacNivelBusca> getNiveisBuscas() {
		return niveisBuscas;
	}

	public void setNiveisBuscas(List<AacNivelBusca> niveisBuscas) {
		this.niveisBuscas = niveisBuscas;
	}

	public List<AacFormaEspecialidade> getFormasEspecialidades() {
		return formasEspecialidades;
	}

	public void setFormasEspecialidades(List<AacFormaEspecialidade> formasEspecialidades) {
		this.formasEspecialidades = formasEspecialidades;
	}

	public AacNivelBusca getNivelBusca() {
		return nivelBusca;
	}

	public void setNivelBusca(AacNivelBusca nivelBusca) {
		this.nivelBusca = nivelBusca;
	}

	public AacFormaEspecialidade getFormaEspecialidade() {
		return formaEspecialidade;
	}

	public void setFormaEspecialidade(AacFormaEspecialidade formaEspecialidade) {
		this.formaEspecialidade = formaEspecialidade;
	}

	public AacNivelBuscaId getIdNivelBusca() {
		return idNivelBusca;
	}

	public void setIdNivelBusca(AacNivelBuscaId idNivelBusca) {
		this.idNivelBusca = idNivelBusca;
	}

	public AacFormaEspecialidadeId getIdFormaEspecialidade() {
		return idFormaEspecialidade;
	}

	public void setIdFormaEspecialidade(AacFormaEspecialidadeId idFormaEspecialidade) {
		this.idFormaEspecialidade = idFormaEspecialidade;
	}

	public boolean isEdicaoNivelBusca() {
		return edicaoNivelBusca;
	}

	public void setEdicaoNivelBusca(boolean edicaoNivelBusca) {
		this.edicaoNivelBusca = edicaoNivelBusca;
	}

	public boolean isEdicaoFormaEspecialidade() {
		return edicaoFormaEspecialidade;
	}

	public void setEdicaoFormaEspecialidade(boolean edicaoFormaEspecialidade) {
		this.edicaoFormaEspecialidade = edicaoFormaEspecialidade;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
}
