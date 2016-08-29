package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelExameConselhoProfs;
import br.gov.mec.aghu.model.AelExameConselhoProfsId;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author amalmeida
 * 
 */

public class ManterConselhosProfissionaisExameController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";

	private static final long serialVersionUID = -7642170471070887605L;

	@EJB
	private IExamesFacade examesFacade;

	private AelExamesMaterialAnalise examesMaterialAnalise;

	private AelExameConselhoProfs exameConselhoProfs;

	private String sigla;

	private Integer manSeq;

	private List<AelExameConselhoProfs> listaConselhosProfsExame;

	private Short codigo;

	private RapConselhosProfissionais rapConselhosProfs;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 


		if (StringUtils.isNotBlank(this.sigla) && this.manSeq != null) {
			this.examesMaterialAnalise = this.examesFacade.buscarAelExamesMaterialAnalisePorId(this.sigla, this.manSeq);
			this.atualizarLista(sigla, manSeq);
			this.exameConselhoProfs = new AelExameConselhoProfs();
		}
	
	}

	/**
	 * Atualiza a lista de Conselhos Profissionais que solicitam exames
	 * 
	 * @param sigla
	 * @param manSeq
	 */
	private void atualizarLista(String sigla, Integer manSeq) {
		this.listaConselhosProfsExame = this.examesFacade.listaConselhosProfsExame(sigla, manSeq);
	}

	/**
	 * Método que realiza a ação do botão gravar na tela de cadastro de Conselhos Profs Exame
	 */
	public void gravar() {

		try {

			// Vincula examesMaterialAnalise com exameConselhoProfs
			this.exameConselhoProfs.setExamesMaterialAnalise(examesMaterialAnalise);
			this.exameConselhoProfs.setConselhosProfissionais(rapConselhosProfs);

			// Submete o conselho profissional para ser persistido
			examesFacade.inserirAelExameConselhoProfs(exameConselhoProfs);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_CONSELHOS_PROFISSIONAIS");

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		limpar();
	}

	public void limpar() {
		this.codigo = null;
		this.exameConselhoProfs = new AelExameConselhoProfs();
		this.rapConselhosProfs = null;
		this.atualizarLista(sigla, manSeq);
	}

	/**
	 * Exclui o registro
	 * 
	 * @param recomendacaoExame
	 */
	public void excluir() {
		try {
			AelExameConselhoProfsId id = new AelExameConselhoProfsId();
			id.setEmaExaSigla(this.sigla);
			id.setEmaManSeq(this.manSeq);
			id.setCprCodigo(this.codigo);
			AelExameConselhoProfs exameConselhoProfs = examesFacade.obterConselhosProfsExamePorID(id);
			examesFacade.removerAelExameConselhoProfs(exameConselhoProfs);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_CONSELHOS_PROFS");
			limpar();
		} finally {
			this.codigo = null;
		}
	}

	public void cancelarExclusao() {
		this.codigo = null;
	}

	/**
	 * Consulta suggestionbox
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<RapConselhosProfissionais> listarConselhosProfsExame(String objPesquisa) {
		try {
			return examesFacade.listarConselhosProfsExame(objPesquisa);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			return new LinkedList<RapConselhosProfissionais>();
		}
	}

	public String voltar() {
		this.examesMaterialAnalise = null;
		this.exameConselhoProfs = null;
		this.sigla = null;
		this.manSeq = null;
		this.listaConselhosProfsExame = null;
		this.codigo = null;
		this.rapConselhosProfs = null;
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	public RapConselhosProfissionais getRapConselhosProfs() {
		return rapConselhosProfs;
	}

	public void setRapConselhosProfs(RapConselhosProfissionais rapConselhosProfs) {
		this.rapConselhosProfs = rapConselhosProfs;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public List<AelExameConselhoProfs> getListaConselhosProfsExame() {
		return listaConselhosProfsExame;
	}

	public void setListaConselhosProfsExame(List<AelExameConselhoProfs> listaConselhosProfsExame) {
		this.listaConselhosProfsExame = listaConselhosProfsExame;
	}

	public AelExamesMaterialAnalise getExamesMaterialAnalise() {
		return examesMaterialAnalise;
	}

	public void setExamesMaterialAnalise(AelExamesMaterialAnalise examesMaterialAnalise) {
		this.examesMaterialAnalise = examesMaterialAnalise;
	}

	public AelExameConselhoProfs getExameConselhoProfs() {
		return exameConselhoProfs;
	}

	public void setExameConselhoProfs(AelExameConselhoProfs exameConselhoProfs) {
		this.exameConselhoProfs = exameConselhoProfs;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

}