package br.gov.mec.aghu.compras.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.compras.dao.ScoMaterialDescTecnicaDAO;
import br.gov.mec.aghu.model.ScoDescricaoTecnicaPadrao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialDescTecnica;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ScoMaterialDescTecnicaRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1007099192678615040L;
	
	@Inject
	private ScoMaterialDescTecnicaDAO scoMaterialDescTecnicaDAO;	

	public void inserirMaterialDescTecnica(final ScoMaterialDescTecnica scoMaterialDescTecnica) {
		getScoMaterialDescTecnicaDAO().persistir(scoMaterialDescTecnica);
	}
	
	public String associarMaterialDescTecnica(final ScoMaterial material, List<ScoDescricaoTecnicaPadrao> listaDescricao) {
		
		List<ScoMaterialDescTecnica> listaVinculacao = getScoMaterialDescTecnicaDAO().buscarListaDescricoesByCodigoMaterial(material);

		List<ScoMaterialDescTecnica> listaRemocao = this.consultarListaRemocao(listaDescricao, listaVinculacao);
		for (ScoMaterialDescTecnica mat : listaRemocao) {
			getScoMaterialDescTecnicaDAO().remover(mat);
		}

		
		List<ScoMaterialDescTecnica> listaInsercao = this.consultarListaInsercao(listaDescricao, listaVinculacao, material);
		for (ScoMaterialDescTecnica mat : listaInsercao) {
			getScoMaterialDescTecnicaDAO().persistir(mat);
		}
		
		getScoMaterialDescTecnicaDAO().flush();
		
		return "MENSAGEM_VINCULACAO_SUCESSO";
	}
	
	private List<ScoMaterialDescTecnica> consultarListaInsercao(List<ScoDescricaoTecnicaPadrao> listaDescricao, List<ScoMaterialDescTecnica> listaVinculacao, ScoMaterial material) {
		List<ScoMaterialDescTecnica> listaInsercao = new ArrayList<ScoMaterialDescTecnica>();
		boolean existe = false;
		
		for (ScoDescricaoTecnicaPadrao descricao: listaDescricao) {
			for (ScoMaterialDescTecnica matdesc : listaVinculacao) {
				if (descricao.getCodigo().equals(matdesc.getDescricao().getCodigo())) {
					existe = true;
					break;
				}
			}
			
			if (!existe) {
				ScoMaterialDescTecnica matdescNovo = new ScoMaterialDescTecnica();
				matdescNovo.setDescricao(descricao);
				matdescNovo.setMaterial(material);
				listaInsercao.add(matdescNovo);
			}
			
			existe = false;
		}
		
		return listaInsercao;
	}
	
	private List<ScoMaterialDescTecnica> consultarListaRemocao(List<ScoDescricaoTecnicaPadrao> listaDescricao, List<ScoMaterialDescTecnica> listaVinculacao) {
		List<ScoMaterialDescTecnica> listaRemocao = new ArrayList<ScoMaterialDescTecnica>();
		boolean existe = false;
		
		for (ScoMaterialDescTecnica descAntiga : listaVinculacao) {
			for (ScoDescricaoTecnicaPadrao descNova : listaDescricao) {
				if (descAntiga.getDescricao().getCodigo().equals(descNova.getCodigo())) {
					existe = true;
					break;
				}
			}
			
			if (!existe) {
				listaRemocao.add(descAntiga);
			}
			
			existe = false;
		}
		
		return listaRemocao;
	}

	
	private ScoMaterialDescTecnicaDAO getScoMaterialDescTecnicaDAO() {
		return scoMaterialDescTecnicaDAO;
	}

	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
}
