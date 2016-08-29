package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.paginator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.ICidFacade;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.model.AghGrupoCids;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;

public class GruposCidPaginator extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -2632783019215539344L;

	@EJB
	private ICidFacade cidFacade;

	private AghCapitulosCid capituloCid;

	private Integer codigo;

	private String sigla;

	private String descricao;

	private DominioSituacao situacao;

	public GruposCidPaginator() {
		// setAtivo(false); // Desabilita a execução da pesquisa logo que entra
		// na tela.
	}

	@Override
	public Long recuperarCount() {

		Long count = cidFacade.pesquisarGruposCidsCount(this.capituloCid, this.codigo, this.sigla, this.descricao, this.situacao);

		return count;
	}

	@Override
	public List<AghGrupoCids> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {

		List<AghGrupoCids> result = this.cidFacade.pesquisarGruposCids(firstResult, maxResults, orderProperty, asc, this.capituloCid, this.codigo, this.sigla,
				this.descricao, this.situacao);

		if (result == null) {
			result = new ArrayList<AghGrupoCids>();
		}

		return result;
	}

	public AghCapitulosCid getCapituloCid() {
		return capituloCid;
	}

	public void setCapituloCid(AghCapitulosCid capituloCid) {
		this.capituloCid = capituloCid;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

}
