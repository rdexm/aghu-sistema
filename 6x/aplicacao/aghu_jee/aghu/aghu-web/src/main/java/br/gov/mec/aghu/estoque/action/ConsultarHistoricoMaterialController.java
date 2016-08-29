package br.gov.mec.aghu.estoque.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.HistoricoScoMaterialVO;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialJN;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;

public class ConsultarHistoricoMaterialController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ConsultarHistoricoMaterialController.class);

	private static final long serialVersionUID = 969183679600766701L;

	@EJB
	private IEstoqueFacade estoqueFacade;

	private ScoMaterial material;

	private Integer codigoMaterial;

	private List<ScoMaterialJN> logsAlteracoesMaterial;
	private List<ScoMaterialJN> logsComparacaoAlteracoesMaterial;

	private List<HistoricoScoMaterialVO> listaItens;

	private String voltarPara;

	public void iniciar() {
	 

	 

		if (this.getCodigoMaterial() != null) {
			this.setMaterial(this.getEstoqueFacade().obterMaterialPorId((this.getCodigoMaterial())));
			this.setLogsAlteracoesMaterial(getEstoqueFacade().pesquisarScoMaterialJNPorCodigoMaterial(this.getCodigoMaterial(),
					DominioOperacoesJournal.UPD));
			this.setLogsComparacaoAlteracoesMaterial(getEstoqueFacade().pesquisarScoMaterialJNPorCodigoMaterial(this.getCodigoMaterial(),
					null));
		}
	
	}
	

	private ScoMaterialJN getScoMaterialJN(Integer indice) {
		try {
			return this.getLogsComparacaoAlteracoesMaterial().get(indice);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void pesquisarAlteracoes(Integer indice) {
		this.limparListaItens();
		ScoMaterialJN atual = this.getScoMaterialJN(indice);
		ScoMaterialJN anterior = this.getScoMaterialJN(indice + 1);
		if (anterior != null) {
			try {
				this.setListaItens(this.getEstoqueFacade().identificarAlteracoesScoMaterial(atual, anterior));
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	private void limparListaItens() {
		if (this.getListaItens() != null) {
			this.setListaItens(null);
		}
	}

	public String voltar() {
		return this.voltarPara;
	}

	public void setEstoqueFacade(IEstoqueFacade estoqueFacade) {
		this.estoqueFacade = estoqueFacade;
	}

	public IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setLogsAlteracoesMaterial(List<ScoMaterialJN> logsAlteracoesMaterial) {
		this.logsAlteracoesMaterial = logsAlteracoesMaterial;
	}

	public List<ScoMaterialJN> getLogsAlteracoesMaterial() {
		return logsAlteracoesMaterial;
	}

	public void setListaItens(List<HistoricoScoMaterialVO> listaItens) {
		this.listaItens = listaItens;
	}

	public List<HistoricoScoMaterialVO> getListaItens() {
		return listaItens;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public List<ScoMaterialJN> getLogsComparacaoAlteracoesMaterial() {
		return logsComparacaoAlteracoesMaterial;
	}

	public void setLogsComparacaoAlteracoesMaterial(List<ScoMaterialJN> logsComparacaoAlteracoesMaterial) {
		this.logsComparacaoAlteracoesMaterial = logsComparacaoAlteracoesMaterial;
	}

}