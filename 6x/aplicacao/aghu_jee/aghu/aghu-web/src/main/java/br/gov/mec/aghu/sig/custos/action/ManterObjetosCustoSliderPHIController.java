package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class ManterObjetosCustoSliderPHIController extends ActionController {



	private static final long serialVersionUID = -7971103433105948303L;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private ManterObjetosCustoController manterObjetosCustoController;

	private SigObjetoCustoPhis sigObjetoCustoPhis;
	private Integer seqPhiExclusao;
	private Boolean definirComoNomeProduto = false;
	private boolean possuiAlteracaoPhi;

	private List<SigObjetoCustoPhis> listaPhis;
	private List<SigObjetoCustoPhis> listaPhisExcluir;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicializarPhi(Integer seqObjetoCustoVersao) {
		this.setSigObjetoCustoPhis(new SigObjetoCustoPhis());
		this.setListaPhis(new ArrayList<SigObjetoCustoPhis>());
		this.setListaPhisExcluir(new ArrayList<SigObjetoCustoPhis>());
		this.setPossuiAlteracaoPhi(false);

		if (seqObjetoCustoVersao != null) {
			// buscar lista phis já associados
			List<SigObjetoCustoPhis> listResult = custosSigFacade.pesquisarPhiPorObjetoCustoVersao(seqObjetoCustoVersao);
			if (listResult != null && !listResult.isEmpty()) {
				this.setListaPhis(listResult);
			}
		}
	}

	public List<FatProcedHospInternos> pesquisarPhis(String paramPesquisa) throws BaseException {
		List<FatProcedHospInternos> listaResultado = new ArrayList<FatProcedHospInternos>();
		listaResultado = this.faturamentoFacade.pesquisarPhis(paramPesquisa, FatProcedHospInternosPai.Fields.DESCRICAO.toString(), DominioSituacao.A);
		return listaResultado;
	}

	public void adicionarPhi() {
		try {
			this.getSigObjetoCustoPhis().setSigObjetoCustoVersoes(this.manterObjetosCustoController.getObjetoCustoVersao());
			
			this.custosSigFacade.validarInclusaoPhiObjetoCusto(this.getSigObjetoCustoPhis(), this.getListaPhis());
			
			this.getSigObjetoCustoPhis().setCriadoEm(new Date());
			try {
				this.getSigObjetoCustoPhis().setRapServidores(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			} catch (ApplicationBusinessException e) {
				this.getSigObjetoCustoPhis().setRapServidores(null);
			}
			this.getSigObjetoCustoPhis().setDominioSituacao(DominioSituacao.A);
			this.getListaPhis().add(this.getSigObjetoCustoPhis());

			if (this.getDefinirComoNomeProduto().equals(Boolean.TRUE)) {
				String nomeObjetoCustoPhi = this.getSigObjetoCustoPhis().getFatProcedHospInternos().getDescricao();
				this.manterObjetosCustoController.getObjetoCustoVersao().getSigObjetoCustos().setNome(nomeObjetoCustoPhi);
			}
			this.setSigObjetoCustoPhis(new SigObjetoCustoPhis());
			this.setDefinirComoNomeProduto(Boolean.FALSE);
			this.setPossuiAlteracaoPhi(true);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void excluirPhi() {
		if (this.getSeqPhiExclusao() != null) {
			this.setPossuiAlteracaoPhi(true);
			for (int i = 0; i < this.getListaPhis().size(); i++) {
				SigObjetoCustoPhis sigObjetoCustoPhis = (SigObjetoCustoPhis) this.getListaPhis().get(i);
				if (sigObjetoCustoPhis.getFatProcedHospInternos().getSeq().equals(this.getSeqPhiExclusao())) {
					if (sigObjetoCustoPhis.getSeq() != null) {
						this.getListaPhisExcluir().add(sigObjetoCustoPhis);
					}
					this.getListaPhis().remove(i);
					break;
				}
			}
		}
	}

	public void gravarPhis() {
		// inclusão
		for (SigObjetoCustoPhis sigObjetoCustoPhis : this.getListaPhis()) {
			sigObjetoCustoPhis.setSigObjetoCustoVersoes(this.manterObjetosCustoController.getObjetoCustoVersao());
			this.custosSigFacade.persistPhi(sigObjetoCustoPhis);
		}
		// exclusão
		for (SigObjetoCustoPhis sigObjetoCustoPhis : this.getListaPhisExcluir()) {
			this.custosSigFacade.excluirPhi(sigObjetoCustoPhis);
		}
		this.setListaPhisExcluir(new ArrayList<SigObjetoCustoPhis>());
		this.setPossuiAlteracaoPhi(false);
	}

	public List<SigObjetoCustoPhis> getListaPhis() {
		return listaPhis;
	}

	public void setListaPhis(List<SigObjetoCustoPhis> listaPhis) {
		this.listaPhis = listaPhis;
	}

	public List<SigObjetoCustoPhis> getListaPhisExcluir() {
		return listaPhisExcluir;
	}

	public void setListaPhisExcluir(List<SigObjetoCustoPhis> listaPhisExcluir) {
		this.listaPhisExcluir = listaPhisExcluir;
	}

	public SigObjetoCustoPhis getSigObjetoCustoPhis() {
		return sigObjetoCustoPhis;
	}

	public void setSigObjetoCustoPhis(SigObjetoCustoPhis sigObjetoCustoPhis) {
		this.sigObjetoCustoPhis = sigObjetoCustoPhis;
	}

	public Integer getSeqPhiExclusao() {
		return seqPhiExclusao;
	}

	public void setSeqPhiExclusao(Integer seqPhiExclusao) {
		this.seqPhiExclusao = seqPhiExclusao;
	}

	public Boolean getDefinirComoNomeProduto() {
		return definirComoNomeProduto;
	}

	public void setDefinirComoNomeProduto(Boolean definirComoNomeProduto) {
		this.definirComoNomeProduto = definirComoNomeProduto;
	}

	public boolean isPossuiAlteracaoPhi() {
		return possuiAlteracaoPhi;
	}

	public void setPossuiAlteracaoPhi(boolean possuiAlteracaoPhi) {
		this.possuiAlteracaoPhi = possuiAlteracaoPhi;
	}

}