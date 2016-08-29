package br.gov.mec.aghu.compras.business;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAnexoDescricaoTecnicaDAO;
import br.gov.mec.aghu.compras.dao.ScoDescricaoTecnicaPadraoDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDescTecnicaDAO;
import br.gov.mec.aghu.model.ScoAnexoDescricaoTecnica;
import br.gov.mec.aghu.model.ScoDescricaoTecnicaPadrao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialDescTecnica;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoDescricaoTecnicaPadraoRN extends BaseBusiness {

	private static final long serialVersionUID = -8480419983465000201L;
	private static final Log LOG = LogFactory.getLog(ScoDescricaoTecnicaPadraoRN.class);
	
	public enum ScoDescricaoTecnicaPadraoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DESCRICAO_NULA;
	}
	@Inject
	private ScoMaterialDescTecnicaDAO scoMaterialDescTecnicaDAO;
	@Inject
	private ScoDescricaoTecnicaPadraoDAO scoDescricaoTecnicaPadraoDAO;
	@Inject
	private ScoAnexoDescricaoTecnicaDAO scoAnexoDescricaoTecnicaDAO;

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public String salvarDescricaoTecnica(ScoDescricaoTecnicaPadrao descricaoTecnica, String descricao, List<ScoMaterial> listaMateriais, List<ScoAnexoDescricaoTecnica> listaAnexo) throws BaseException {
		String mensagem = null;

		if (StringUtils.isEmpty(descricao)) {
			throw new ApplicationBusinessException(ScoDescricaoTecnicaPadraoRNExceptionCode.MENSAGEM_DESCRICAO_NULA);
		}
		
		boolean novo = true;
		
		if (descricaoTecnica.getCodigo() != null) {
			novo = false;
		}

		// Dividir a descricao nas duas colunas.
		if (descricao != null && descricao.length() > 2000) { 
			descricaoTecnica.setDescricao1(descricao.substring(0, 2000));
			descricaoTecnica.setDescricao2(descricao.substring(2000)); 
		} else {
			descricaoTecnica.setDescricao1(descricao);
			descricaoTecnica.setDescricao2(" ");
		}

		Set<ScoMaterial> setMateriais = new LinkedHashSet<ScoMaterial>(listaMateriais);
		Set<ScoAnexoDescricaoTecnica> setAnexos = new LinkedHashSet<ScoAnexoDescricaoTecnica>(listaAnexo);
		
		if (!novo) {
			mensagem = this.atualizarDescricaoTecnica(descricaoTecnica, descricao, setMateriais, setAnexos);
		} else {
			mensagem = this.persistirDescricaoTecnica(descricaoTecnica, descricao, setMateriais, setAnexos);
		}

		return mensagem;
	}
	
	public String persistirDescricaoTecnica(ScoDescricaoTecnicaPadrao descricaoTecnica, String descricao, Set<ScoMaterial> listaMateriais, Set<ScoAnexoDescricaoTecnica> listaAnexo) {

		getScoDescricaoTecnicaPadraoDAO().persistir(descricaoTecnica);
		getScoAnexoDescricaoTecnicaDAO().flush();

		// Para cada material presente na lista de materiais vinculados, criar uma vinculação e persistir no banco.
		for (ScoMaterial material : listaMateriais) {
			ScoMaterialDescTecnica matdesc = new ScoMaterialDescTecnica();
			matdesc.setDescricao(descricaoTecnica);
			matdesc.setMaterial(material);

			getScoMaterialDescTecnicaDAO().persistir(matdesc);
			getScoMaterialDescTecnicaDAO().flush();
		}

		for (ScoAnexoDescricaoTecnica anexo : listaAnexo) {
			anexo.setDescricaoTecnica(descricaoTecnica);
			getScoAnexoDescricaoTecnicaDAO().persistir(anexo);
			getScoAnexoDescricaoTecnicaDAO().flush();
		}

		return "MENSAGEM_DESCRICAO_TECNICA_GRAVADA_SUCESSO";
	}

	public String atualizarDescricaoTecnica(ScoDescricaoTecnicaPadrao descricaoTecnica, String descricao, Set<ScoMaterial> listaMateriais, Set<ScoAnexoDescricaoTecnica> listaAnexo) {

		getScoDescricaoTecnicaPadraoDAO().atualizar(descricaoTecnica);
		getScoDescricaoTecnicaPadraoDAO().flush();

		// Consulta as listas de inserção e remoção de materiais. RN06
		List<ScoMaterialDescTecnica> listaInsercaoMateriais = consultarListaInsercaoMateriais(listaMateriais, descricaoTecnica.getListaMateriais(), descricaoTecnica);
		List<ScoMaterialDescTecnica> listaRemocaoMateriais = consultarListaRemocaoMateriais(listaMateriais, descricaoTecnica.getListaMateriais());

		// Aplica as remoções no banco. RN06
		for (ScoMaterialDescTecnica matdesc : listaRemocaoMateriais) {
			getScoMaterialDescTecnicaDAO().remover(matdesc);
		}

		// Aplica as inserções no banco. RN06
		for (ScoMaterialDescTecnica matdesc : listaInsercaoMateriais) {
			getScoMaterialDescTecnicaDAO().persistir(matdesc);
		}

		getScoMaterialDescTecnicaDAO().flush();

		// Consulta as listas de inserção e remoção dos anexos. RN07
		List<ScoAnexoDescricaoTecnica> listaInsercaoAnexos = consultarListaInsercaoAnexos(listaAnexo, descricaoTecnica.getListaAnexo(), descricaoTecnica);
		List<ScoAnexoDescricaoTecnica> listaRemocaoAnexos = consultarListaRemocaoAnexos(listaAnexo, descricaoTecnica.getListaAnexo());

		// Aplica as remoções no banco. RN07
		for (ScoAnexoDescricaoTecnica anexo : listaRemocaoAnexos) {
			anexo = this.getScoAnexoDescricaoTecnicaDAO().obterPorChavePrimaria(anexo.getCodigo());
			getScoAnexoDescricaoTecnicaDAO().remover(anexo);
		}

		// Aplica as inserções no banco. RN07
		for (ScoAnexoDescricaoTecnica anexo : listaInsercaoAnexos) {
			getScoAnexoDescricaoTecnicaDAO().persistir(anexo);
		}

		getScoAnexoDescricaoTecnicaDAO().flush();

		return "MENSAGEM_DESCRICAO_ATUALIZADA_SUCESSO";
	}

	private List<ScoAnexoDescricaoTecnica> consultarListaRemocaoAnexos(Set<ScoAnexoDescricaoTecnica> listaAnexo, Set<ScoAnexoDescricaoTecnica> listaVinculacao) {
		List<ScoAnexoDescricaoTecnica> listaRemocao = new ArrayList<ScoAnexoDescricaoTecnica>();
		boolean existe = false;

		if(listaVinculacao!=null && !listaVinculacao.isEmpty()){

			for (ScoAnexoDescricaoTecnica anexoAntigo : listaVinculacao) {
				for (ScoAnexoDescricaoTecnica anexoNovo : listaAnexo) {
					if (anexoAntigo.getArquivo().equals(anexoNovo.getArquivo())) {
						existe = true;
						break;
					}
				}

				if (!existe) {
					listaRemocao.add(anexoAntigo);
				}

				existe = false;
			}

		}
		return listaRemocao;
	}

	private List<ScoAnexoDescricaoTecnica> consultarListaInsercaoAnexos(Set<ScoAnexoDescricaoTecnica> listaAnexo, Set<ScoAnexoDescricaoTecnica> listaVinculacao, ScoDescricaoTecnicaPadrao descricaoTecnica) {
		List<ScoAnexoDescricaoTecnica> listaInsercao = new ArrayList<ScoAnexoDescricaoTecnica>();
		boolean existe = false;

		for (ScoAnexoDescricaoTecnica anexoNovo : listaAnexo) {
			if(listaVinculacao!=null && !listaVinculacao.isEmpty()){
				for (ScoAnexoDescricaoTecnica anexoAntigo : listaVinculacao) {
					if (anexoNovo.getArquivo().equals(anexoAntigo.getArquivo())) {
						existe = true;
						break;
					}
				}
				
			}

			if (!existe) {
				anexoNovo.setDescricaoTecnica(descricaoTecnica);
				listaInsercao.add(anexoNovo);
			}

			existe = false;
		}

		return listaInsercao;
	}

	private List<ScoMaterialDescTecnica> consultarListaRemocaoMateriais(Set<ScoMaterial> listaMateriais, Set<ScoMaterialDescTecnica> listaVinculacao) {
		List<ScoMaterialDescTecnica> listaRemocao = new ArrayList<ScoMaterialDescTecnica>();
		boolean existe = false;

		if(listaVinculacao!=null && !listaVinculacao.isEmpty()){

			for (ScoMaterialDescTecnica matdesc : listaVinculacao) {
				for (ScoMaterial material : listaMateriais) {
					if (matdesc.getMaterial().getCodigo().equals(material.getCodigo())) {
						existe = true;
						break;
					}
				}


				if (!existe) {
					listaRemocao.add(matdesc);
				}

				existe = false;
			}
		}
		return listaRemocao;
	}

	private List<ScoMaterialDescTecnica> consultarListaInsercaoMateriais(Set<ScoMaterial> listaMateriais, Set<ScoMaterialDescTecnica> listaVinculacao, ScoDescricaoTecnicaPadrao descricaoTecnica) {
		List<ScoMaterialDescTecnica> listaInsercao = new ArrayList<ScoMaterialDescTecnica>();
		boolean existe = false;

		for (ScoMaterial material : listaMateriais) {
			if(listaVinculacao!=null && !listaVinculacao.isEmpty()){
				for (ScoMaterialDescTecnica matdesc : listaVinculacao) {
					if (material.getCodigo().equals(matdesc.getMaterial().getCodigo())) {
						existe = true;
						break;
					}
				}
			}

			if (!existe) {
				ScoMaterialDescTecnica matdescNovo = new ScoMaterialDescTecnica();
				matdescNovo.setDescricao(descricaoTecnica);
				matdescNovo.setMaterial(material);
				listaInsercao.add(matdescNovo);
			}

			existe = false;
		}

		return listaInsercao;
	}


	private ScoMaterialDescTecnicaDAO getScoMaterialDescTecnicaDAO() {
		return scoMaterialDescTecnicaDAO;
	}

	private ScoDescricaoTecnicaPadraoDAO getScoDescricaoTecnicaPadraoDAO() {
		return scoDescricaoTecnicaPadraoDAO;
	}

	private ScoAnexoDescricaoTecnicaDAO getScoAnexoDescricaoTecnicaDAO() {
		return scoAnexoDescricaoTecnicaDAO;
	}
	public void deletarDescricaoTecnica(Short codigo) {

		ScoDescricaoTecnicaPadrao descricaoTecnica = getScoDescricaoTecnicaPadraoDAO().obterPorChavePrimaria(codigo);
		if (descricaoTecnica != null) {
			getScoDescricaoTecnicaPadraoDAO().remover(descricaoTecnica);
			getScoDescricaoTecnicaPadraoDAO().flush();
		}

	}
}