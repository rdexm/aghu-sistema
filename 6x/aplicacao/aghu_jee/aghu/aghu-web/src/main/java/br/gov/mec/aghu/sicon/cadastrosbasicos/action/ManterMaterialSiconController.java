package br.gov.mec.aghu.sicon.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCatalogoSicon;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialSicon;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterMaterialSiconController extends ActionController {

	private static final long serialVersionUID = 8447724304231239484L;

/*	private static final String PAGE_PESQUISAR_MATERIAL_SICON = "pesquisarMaterialSicon";

	private static final String PAGE_GERENCIAR_CONTRATOS = "sicon-gerenciarContratos";*/

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

	@EJB
	private IComprasFacade comprasFacade;


	private ScoMaterialSicon materialSicon = new ScoMaterialSicon();
	private Integer seqSicon;
	private ScoMaterial material;
	private ScoCatalogoSicon catalogoSiconMaterial;
	private Integer codigoSicon;
	private DominioSituacao situacao;
	private boolean alterar;
	private String origem;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 


		if (this.codigoSicon != null) {
			catalogoSiconMaterial = cadastrosBasicosSiconFacade.obterCatalogoSicon(codigoSicon);
			
			if (catalogoSiconMaterial != null) {
				this.materialSicon = this.cadastrosBasicosSiconFacade.obterMaterialSicon(seqSicon);
				this.setMaterial(this.materialSicon.getMaterial());
			}else{
				apresentarExcecaoNegocio(new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO));
				materialSicon = new ScoMaterialSicon();
				this.setSituacao(DominioSituacao.A);
			}

		} else {
			materialSicon = new ScoMaterialSicon();
			this.setSituacao(DominioSituacao.A);
		}
	
	}

	public String salvar() {
		if (catalogoSiconMaterial == null) {
			this.apresentarMsgNegocio(Severity.INFO, "INFORME_CODIGO_SICON_VALIDO");

			this.materialSicon = null;
			this.codigoSicon = null;
			this.material = null;
			this.situacao = null;

		} else {

			try {

				this.materialSicon.setCodigoSicon(this.catalogoSiconMaterial.getCodigoSicon());
				this.materialSicon.setMaterial(this.getMaterial());
				this.materialSicon.setSituacao(this.getSituacao());

				if (isAlterar()) {
					this.cadastrosBasicosSiconFacade.alterarMaterialSicon(this.materialSicon);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_MATERIAL_SICON");

				} else {
					this.cadastrosBasicosSiconFacade.inserirMaterialSicon(this.materialSicon);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_MATERIAL_SICON");
				}

			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				return null;
			}
		}

		return origem;
	}

	public String voltar() {

		materialSicon = new ScoMaterialSicon();
		setCodigoSicon(null);
		setMaterial(null);
		setSituacao(null);
		catalogoSiconMaterial = null;

		return origem;

	}

	public List<ScoMaterial> pesquisarMateriais(String _input) {

		List<ScoMaterial> listaMaterial = null;

		try {
			listaMaterial = this.cadastrosBasicosSiconFacade.pesquisarMateriaisPorFiltro(_input);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return  this.returnSGWithCount(listaMaterial,pesquisarMateriaisCount(_input));
	}

	public List<ScoCatalogoSicon> listarCodigoSiconMaterialAtivo(String pesquisa) {
		String strParametro = (String) pesquisa;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(strParametro)) {
			codigo = Integer.valueOf(strParametro);
		}

		if (codigo == null) {
			if (strParametro.length() < 3) {
				return  this.returnSGWithCount(null,listarCodigoSiconMaterialAtivoCount(pesquisa));
			}
		}

		List<ScoCatalogoSicon> catalogoSiconMaterial = cadastrosBasicosSiconFacade.listarCatalogoSiconMaterialAtivo(pesquisa);

		return catalogoSiconMaterial;
	}
	
	public Long listarCodigoSiconMaterialAtivoCount(String pesquisa) {
		String strParametro = (String) pesquisa;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(strParametro)) {
			codigo = Integer.valueOf(strParametro);
		}

		if (codigo == null) {
			if (strParametro.length() < 3) {
				return null;
			}
		}
		
		return cadastrosBasicosSiconFacade.listarCatalogoSiconMaterialAtivoCount(pesquisa);
	}

	public Long pesquisarMateriaisCount(String _input) {

		return this.comprasFacade.pesquisarMateriaisPorFiltroCount(_input);
	}

	public ICadastrosBasicosSiconFacade getCadastrosBasicosSiconFacade() {
		return cadastrosBasicosSiconFacade;
	}

	public void setCadastrosBasicosSiconFacade(ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade) {
		this.cadastrosBasicosSiconFacade = cadastrosBasicosSiconFacade;
	}

	public ScoMaterialSicon getMaterialSicon() {
		return materialSicon;
	}

	public void setMaterialSicon(ScoMaterialSicon materialSicon) {
		this.materialSicon = materialSicon;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoCatalogoSicon getCatalogoSiconMaterial() {
		return catalogoSiconMaterial;
	}

	public void setCatalogoSiconMaterial(ScoCatalogoSicon catalogoSiconMaterial) {
		this.catalogoSiconMaterial = catalogoSiconMaterial;
	}

	public Integer getCodigoSicon() {
		return codigoSicon;
	}

	public void setCodigoSicon(Integer codigoSicon) {
		this.codigoSicon = codigoSicon;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Integer getSeqSicon() {
		return seqSicon;
	}

	public void setSeqSicon(Integer seqSicon) {
		this.seqSicon = seqSicon;
	}

}
