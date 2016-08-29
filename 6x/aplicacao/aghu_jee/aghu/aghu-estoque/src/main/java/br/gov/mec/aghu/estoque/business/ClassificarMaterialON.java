package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.vo.ClassificacaoMaterialVO;
import br.gov.mec.aghu.estoque.vo.MaterialClassificacaoVO;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoes;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoesId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável pelas regras de FORMs e montagem de VOs da estoria #6621 -
 * Classificar Material
 * 
 * @author rpanassolo
 * 
 */
@Stateless
public class ClassificarMaterialON extends BaseBusiness {

	private static final long serialVersionUID = 2771985552789896042L;

	private static final Log LOG = LogFactory.getLog(ClassificarMaterialON.class);

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private ScoMaterialRN scoMaterialRN;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public void adicionarMaterialClassificacao(Long cn5Numero, Integer matCodigo, ScoMaterial material, String nomeMicrocomputador) throws BaseException {
		ScoMateriaisClassificacoes materialClassificacao = new ScoMateriaisClassificacoes();
		ScoMateriaisClassificacoesId id = new ScoMateriaisClassificacoesId();
		id.setCn5Numero(cn5Numero);
		id.setMatCodigo(matCodigo);
		materialClassificacao.setId(id);
		materialClassificacao.setMaterial(material);
		inserirMateriaisClassificacao(materialClassificacao, nomeMicrocomputador);

	}
	
	private void inserirMateriaisClassificacao(ScoMateriaisClassificacoes materialClassificacao, String nomeMicrocomputador) throws BaseException {
		// Chamada para RNs com regras de banco
		this.getScoMaterialRN().getScoMateriaisClassificacaoRN().inserir(materialClassificacao, nomeMicrocomputador);
	}

	public void removerMateriaisClassificacao(Object idRemocao) throws BaseException {
		ScoMateriaisClassificacoes materialClassificacao = getComprasFacade().obterScoMateriaisClassificacoes(idRemocao);
		deleteMateriaisClassificacao(materialClassificacao);
	}
	
	private void deleteMateriaisClassificacao(ScoMateriaisClassificacoes materialClassificacao)
			throws BaseException {
		// Chamada para RNs com regras de banco
		this.getScoMaterialRN().getScoMateriaisClassificacaoRN().remover(materialClassificacao);
	}

	// #6621 C2
	public List<ClassificacaoMaterialVO> pesquisarClassificacaoDoMaterial(Integer codMaterial) {
		List<ClassificacaoMaterialVO> vos = new ArrayList<ClassificacaoMaterialVO>();
		List<ScoMateriaisClassificacoes> classifica = this.getComprasFacade().pesquisarScoMateriaisClassificacoesPorMaterial(codMaterial);
		for (ScoMateriaisClassificacoes scoMateriaisClassificacoes : classifica) {
			ScoClassifMatNiv5 scoClassifMatNiv5 = this.getComprasFacade().obterClassifMatNiv5PorNumero(
					scoMateriaisClassificacoes.getId().getCn5Numero());
			ClassificacaoMaterialVO classificaVO = popularClassificacaoMaterialVO(scoClassifMatNiv5);
			vos.add(classificaVO);
		}
		return vos;
	}

	private ClassificacaoMaterialVO popularClassificacaoMaterialVO(ScoClassifMatNiv5 scoClassifMatNiv5) {
		ClassificacaoMaterialVO classificaVO = new ClassificacaoMaterialVO();
		classificaVO.setDescricao(obterDescricaoON(scoClassifMatNiv5));
		classificaVO.setNumero(scoClassifMatNiv5.getNumero());
		classificaVO.setNivel1(scoClassifMatNiv5.getScoClassifMatNiv4().getId().getCn3Cn2Cn1Codigo());
		classificaVO.setNivel2(scoClassifMatNiv5.getScoClassifMatNiv4().getId().getCn3Cn2Codigo());
		classificaVO.setNivel3(scoClassifMatNiv5.getScoClassifMatNiv4().getId().getCn3Codigo());
		classificaVO.setNivel4(scoClassifMatNiv5.getScoClassifMatNiv4().getId().getCodigo());
		classificaVO.setNivel5(scoClassifMatNiv5.getCodigo());
		classificaVO.setCodGrupo(scoClassifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getScoClassifMatNiv2()
				.getScoClassifMatNiv1().getGrupoMaterial().getCodigo());
		return classificaVO;
	}

	// #6621 ON1
	private String obterDescricaoON(ScoClassifMatNiv5 scoClassifMatNiv5) {
		if (scoClassifMatNiv5.getCodigo() != 0) {
			return scoClassifMatNiv5.getDescricao();
		}
		if (scoClassifMatNiv5.getScoClassifMatNiv4().getId().getCodigo() != 0) {
			return scoClassifMatNiv5.getScoClassifMatNiv4().getDescricao();
		}

		if (scoClassifMatNiv5.getScoClassifMatNiv4().getId().getCn3Codigo() != 0) {
			return scoClassifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getDescricao();
		}

		if (scoClassifMatNiv5.getScoClassifMatNiv4().getId().getCn3Cn2Codigo() != 0) {
			return scoClassifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getScoClassifMatNiv2().getDescricao();
		}

		if (scoClassifMatNiv5.getScoClassifMatNiv4().getId().getCn3Cn2Cn1Codigo() != 0) {
			return scoClassifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getScoClassifMatNiv2().getScoClassifMatNiv1()
					.getDescricao();
		}

		return scoClassifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getScoClassifMatNiv2().getScoClassifMatNiv1()
				.getGrupoMaterial().getDescricao();
	}

	// #6621 C3
	public List<ClassificacaoMaterialVO> pesquisarClassificacoes(Integer codGrupo, Object parametro) {
		List<ClassificacaoMaterialVO> vos = new ArrayList<ClassificacaoMaterialVO>();
		List<ScoClassifMatNiv5> scoClassifMatNiv5s = this.getComprasFacade().listarClassifMatNiv5PorGrupo(codGrupo, parametro);
		for (ScoClassifMatNiv5 scoClassifMatNiv5 : scoClassifMatNiv5s) {
			ClassificacaoMaterialVO classificaVO = popularClassificacaoMaterialVO(scoClassifMatNiv5);
			vos.add(classificaVO);
		}
		return vos;
	}

	// #6621 C3 count
	public Long pesquisarClassificacoesCount(Integer codGrupo, Object parametro) {
		return this.getComprasFacade().listarClassifMatNiv5PorGrupoCount(codGrupo, parametro);
	}

	public List<MaterialClassificacaoVO> listarMateriasPorClassificacao(Long cn5, Integer codGrupo) {
		return this.getComprasFacade().listarMateriasPorClassificacao(cn5, codGrupo);
	}

	/**
	 * get de RNs e DAOs
	 */
	private ScoMaterialRN getScoMaterialRN() {
		return scoMaterialRN;
	}

	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}
}
