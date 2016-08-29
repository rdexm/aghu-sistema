package br.gov.mec.aghu.exames.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelApXPatologistaId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;

public class InformarResponsavelAPController extends ActionController {

	private static final long serialVersionUID = 0L;

	private static final String LISTAR_AMOSTRAS_SOLICITACAO_RECEBIMENTO = "listarAmostrasSolicitacaoRecebimento";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IExamesFacade examesFacade;

	private Integer soeSeq;
	private Integer seqp;
	
	private AelItemSolicitacaoExames itemSolicExames;

	private AelApXPatologista apXPatologista;

	private List<AelPatologista> lista;
	
	List<AelAmostraItemExames> listaExamesAmostras;

	private String nomePaciente;

	private Integer seqExcluir;
	
	private Boolean edicao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		List<AelAmostraItemExames> listaExamesAmostrasAux = this.examesFacade.buscarAelAmostraItemExamesPorAmostraComApNotNull(soeSeq, seqp);
		itemSolicExames = null;
		this.listaExamesAmostras = new ArrayList<AelAmostraItemExames>();
		this.listaExamesAmostras.add(listaExamesAmostrasAux.get(0));
		if (listaExamesAmostras.size() > 1) {
			edicao = false;
		}
		else {
			pesquisarNumeroAp(listaExamesAmostras.get(0).getAelItemSolicitacaoExames());
		}
	
	}
	
	public void pesquisarNumeroAp(AelItemSolicitacaoExames itemSolicExames) {
		this.itemSolicExames = itemSolicExames;
		this.apXPatologista = new AelApXPatologista();

		AelAnatomoPatologico aelAnatomoPatologico = examesPatologiaFacade.obterAelAnatomoPatologicoPorItemSolic(itemSolicExames.getId().getSoeSeq(), itemSolicExames.getId().getSeqp());
		
		this.apXPatologista.setAelAnatomoPatologicos(aelAnatomoPatologico);
		final AelSolicitacaoExames solicitacaoExame = this.examesFacade.obterAelSolicitacaoExamesPeloId(this.soeSeq);
		this.nomePaciente = examesFacade.buscarLaudoNomePaciente(solicitacaoExame);
		this.carregarLista();

		edicao = true;
	}

	private void carregarLista() {
		if (this.apXPatologista != null && this.apXPatologista.getAelAnatomoPatologicos() != null) {
			this.lista = examesFacade.buscarPatologistasPorAnatomoPatologicos(this.apXPatologista.getAelAnatomoPatologicos().getSeq());
		}
	}

	public void gravar() {
		if (this.apXPatologista != null && this.apXPatologista.getAelAnatomoPatologicos() != null && this.apXPatologista.getAelPatologista() != null) {
			if (this.lista.contains(this.apXPatologista.getAelPatologista())) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PATOLOGISTA_ASSOCIADO",
						this.apXPatologista.getAelPatologista().getNome());
			} else {
				try {
					
					this.apXPatologista.setId(new AelApXPatologistaId(this.apXPatologista.getAelAnatomoPatologicos().getSeq(), this.apXPatologista.getAelPatologista().getSeq()));
					this.examesPatologiaFacade.persistirAelApXPatologista(apXPatologista);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AP_X_PATOLOGIA_INSERT_SUCESSO", apXPatologista.getAelPatologista().getNome());
					this.carregarLista();
					this.apXPatologista = new AelApXPatologista();
					
					AelAnatomoPatologico aelAnatomoPatologico = examesPatologiaFacade.obterAelAnatomoPatologicoPorItemSolic(itemSolicExames.getId().getSoeSeq(), itemSolicExames.getId().getSeqp());
					
					this.apXPatologista.setAelAnatomoPatologicos(examesPatologiaFacade.obterAelAnatomoPatologicoByNumeroAp(
							aelAnatomoPatologico.getNumeroAp(),
							aelAnatomoPatologico.getConfigExame().getSeq()));
					
				} catch (final BaseRuntimeException e) {
					apresentarExcecaoNegocio(e);
				} catch (final BaseException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		}
	}

	public void excluir() {
		if (this.seqExcluir != null) {
			try {
				examesPatologiaFacade.excluirAelApXPatologistaPorPatologista(seqExcluir, apXPatologista.getAelAnatomoPatologicos().getSeq());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AP_X_PATOLOGIA_EXCLUIDO_SUCESSO");
				this.carregarLista();
			} catch (final ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public String voltar() {
		return LISTAR_AMOSTRAS_SOLICITACAO_RECEBIMENTO;
	}

	public List<AelPatologista> pesquisarResponsavelApPorCodigoEDescricao(final String parametro) {
		return this.examesPatologiaFacade.pesquisarPatologistasPorNomeESituacao(parametro, DominioSituacao.A, 100);
	}
	
	public Long getNumeroApItemSolicitacao(AelItemSolicitacaoExames itemSolicExames) {
		if (itemSolicExames != null){
			AelAnatomoPatologico aelAnatomoPatologico = examesPatologiaFacade.obterAelAnatomoPatologicoPorItemSolic(itemSolicExames.getId().getSoeSeq(), itemSolicExames.getId().getSeqp());
			return aelAnatomoPatologico.getNumeroAp();
		} else {
			return null;
		}
	}
	
	public void setApXPatologista(final AelApXPatologista apXPatologista) {
		this.apXPatologista = apXPatologista;
	}

	public AelItemSolicitacaoExames getItemSolicExames() {
		return itemSolicExames;
	}

	public void setItemSolicExames(AelItemSolicitacaoExames itemSolicExames) {
		this.itemSolicExames = itemSolicExames;
	}

	public AelApXPatologista getApXPatologista() {
		return apXPatologista;
	}

	public void setNomePaciente(final String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setSoeSeq(final Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setLista(final List<AelPatologista> lista) {
		this.lista = lista;
	}

	public List<AelPatologista> getLista() {
		return lista;
	}

	public void setSeqExcluir(final Integer seqExcluir) {
		this.seqExcluir = seqExcluir;
	}

	public Integer getSeqExcluir() {
		return seqExcluir;
	}

	public List<AelAmostraItemExames> getListaExamesAmostras() {
		return listaExamesAmostras;
	}

	public void setListaExamesAmostras(
			List<AelAmostraItemExames> listaExamesAmostras) {
		this.listaExamesAmostras = listaExamesAmostras;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}
}