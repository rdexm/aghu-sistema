package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.hibernate.Hibernate;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghGrupoCids;



@Modulo(ModuloEnum.INTERNACAO)

@Stateless
public class CidFacade extends BaseFacade implements ICidFacade {


@EJB
private GrupoCidCRUD grupoCidCRUD;

@EJB
private CidsON cidsON;

@EJB
private CapitulosCidCRUD capitulosCidCRUD;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3598201121705401518L;

	/**
	 * @param firstResult
	 * @param maxResults
	 * @param numero
	 * @param descricao
	 * @param indExigeCidSecundario
	 * @param indSituacao
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CapitulosCidCRUD#pesquisar(java.lang.Integer, java.lang.Integer, java.lang.Short, java.lang.String, br.gov.mec.aghu.dominio.DominioSimNao, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	@Secure("#{s:hasPermission('capituloCid','pesquisar')}")
	public List<AghCapitulosCid> pesquisar(Integer firstResult, Integer maxResults, Short numero, String descricao,
			DominioSimNao indExigeCidSecundario, DominioSituacao indSituacao) {
		List<AghCapitulosCid> capitulosCid = this.getCapitulosCidCRUD().pesquisar(firstResult, maxResults, numero, descricao, indExigeCidSecundario, indSituacao);
		
		for (AghCapitulosCid capituloCid : capitulosCid) {
			Hibernate.initialize(capituloCid.getRapServidor());
			if (capituloCid.getRapServidor() != null) {
				Hibernate.initialize(capituloCid.getRapServidor().getPessoaFisica());
			}
		}
		
		return capitulosCid;
	}

	/**
	 * @param numero
	 * @param descricao
	 * @param indExigeCidSecundario
	 * @param indSituacao
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CapitulosCidCRUD#obterCapituloCidCount(java.lang.Short, java.lang.String, br.gov.mec.aghu.dominio.DominioSimNao, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	public Long obterCapituloCidCount(Short numero, String descricao, DominioSimNao indExigeCidSecundario,
			DominioSituacao indSituacao) {
		return this.getCapitulosCidCRUD().obterCapituloCidCount(numero, descricao, indExigeCidSecundario, indSituacao);
	}

	/**
	 * @param capituloCid
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CapitulosCidCRUD#persistirCapituloCid(br.gov.mec.aghu.model.AghCapitulosCid)
	 */
	@Override
	@Secure("#{s:hasPermission('capituloCid','alterar')}")
	public void persistirCapituloCid(AghCapitulosCid capituloCid) throws ApplicationBusinessException {
		this.getCapitulosCidCRUD().persistirCapituloCid(capituloCid);
	}

	/**
	 * @param seq
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CapitulosCidCRUD#obterCapituloCid(java.lang.Integer)
	 */
	@Override
	@Secure("#{s:hasPermission('capituloCid','pesquisar')}")
	public AghCapitulosCid obterCapituloCid(Integer seq) {
		return this.getCapitulosCidCRUD().obterCapituloCid(seq);
	}

	/**
	 * @param capituloCidSeq
	 * @throws ApplicationBusinessException
	 * @see {@link br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CapitulosCidCRUD#removerCapituloCid(Integer)}
	 */
	@Override
	@Secure("#{s:hasPermission('capituloCid','excluir')}")
	public void removerCapituloCid(Integer capituloCidSeq) throws ApplicationBusinessException {
		this.getCapitulosCidCRUD().removerCapituloCid(capituloCidSeq);
	}

	/**
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CapitulosCidCRUD#pesquisarCapitulosCidsAtivo()
	 */
	@Override
	@Secure("#{s:hasPermission('capituloCid','pesquisar')}")
	public List<AghCapitulosCid> pesquisarCapitulosCidsAtivo() {
		return this.getCapitulosCidCRUD().pesquisarCapitulosCidsAtivo();
	}

	/**
	 * @param codigo
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#obterCid(java.lang.String)
	 */
	@Override
	@Secure("#{s:hasPermission('cid','pesquisar')}")
	public AghCid obterCid(String codigo) {
		AghCid cid = this.getCidsON().obterCid(codigo);
		inicializarAtributosCID(cid);
		return cid;
	}

	/**
	 * @param seq
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#obterCidporSeq(java.lang.Integer)
	 */
	@Override
	@Secure("#{s:hasPermission('cid','pesquisar')}")
	public AghCid obterCidporSeq(Integer seq) {
		return this.getCidsON().obterCidporSeq(seq);
	}

	/**
	 * @param seq
	 * @param codigo
	 * @param descricao
	 * @param situacaoPesquisa
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#pesquisaCount(java.lang.Integer, java.lang.String, java.lang.String, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	public Long pesquisaCount(Integer seq, String codigo, String descricao, DominioSituacao situacaoPesquisa) {
		return this.getCidsON().pesquisaCount(seq, codigo, descricao, situacaoPesquisa);
	}

	/**
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param seq
	 * @param codigo
	 * @param descricao
	 * @param situacaoPesquisa
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#pesquisa(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, java.lang.Integer, java.lang.String, java.lang.String, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	@Secure("#{s:hasPermission('cid','pesquisar')}")
	public List<AghCid> pesquisa(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Integer seq,
			String codigo, String descricao, DominioSituacao situacaoPesquisa) {
		List<AghCid> cids = this.getCidsON().pesquisa(firstResult, maxResults, orderProperty, asc, seq, codigo, descricao, situacaoPesquisa);
		
		for (AghCid cid : cids) {
			inicializarAtributosCID(cid);
		}
		
		return cids;
	}

	private void inicializarAtributosCID(AghCid cid) {
		Hibernate.initialize(cid.getGrupoCids());
//		Hibernate.initialize(cid.getAltaDiagPrincipais());
//		Hibernate.initialize(cid.getAltaDiagSecundarios());
		Hibernate.initialize(cid.getCid());
		Hibernate.initialize(cid.getCids());
		Hibernate.initialize(cid.getCidsInternacao());
		Hibernate.initialize(cid.getCidUsualEquipes());
		Hibernate.initialize(cid.getDiagnosticoDescricoes());
		Hibernate.initialize(cid.getDiagnosticos());
		Hibernate.initialize(cid.getMotivoAtendimentos());
		Hibernate.initialize(cid.getServidor());
		if (cid.getServidor() != null) {
			Hibernate.initialize(cid.getServidor().getPessoaFisica());
		}
	}
	
	/**
	 * @param cid
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#excluirCid(br.gov.mec.aghu.model.AghCid)
	 */
	@Override
	@Secure("#{s:hasPermission('cid','excluir')}")
	public void excluirCid(AghCid cid) throws ApplicationBusinessException {
		this.getCidsON().excluirCid(cid);
	}

	/**
	 * @param cid
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#incluirCid(br.gov.mec.aghu.model.AghCid)
	 */
	@Override
	@Secure("#{s:hasPermission('cid','alterar')}")
	public void incluirCid(AghCid cid) throws ApplicationBusinessException {
		this.getCidsON().incluirCid(cid);
	}

	/**
	 * @param cid
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#atualizarCid(br.gov.mec.aghu.model.AghCid)
	 */
	@Override
	@Secure("#{s:hasPermission('cid','alterar')}")
	public void atualizarCid(AghCid cid) throws ApplicationBusinessException {
		this.getCidsON().atualizarCid(cid);
	}

	/**
	 * @param descricao
	 * @param limiteRegistros
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#pesquisarCidsSemSubCategoriaPorDescricaoOuId(java.lang.String, java.lang.Integer)
	 */
	@Override
	@Secure("#{s:hasPermission('cid','pesquisar')}")
	public List<AghCid> pesquisarCidsSemSubCategoriaPorDescricaoOuId(String descricao, Integer limiteRegistros) {
		return this.getCidsON().pesquisarCidsSemSubCategoriaPorDescricaoOuId(descricao, limiteRegistros);
	}

	/**
	 * @param descricao
	 * @param limiteRegistros
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#pesquisarCidsComSubCategoriaPorDescricaoOuId(java.lang.String, java.lang.Integer)
	 */
	@Override
	@Secure("#{s:hasPermission('cid','pesquisar')}")
	public List<AghCid> pesquisarCidsComSubCategoriaPorDescricaoOuId(String descricao, Integer limiteRegistros) {
		return this.getCidsON().pesquisarCidsComSubCategoriaPorDescricaoOuId(descricao, limiteRegistros);
	}

	/**
	 * @return
	 *  
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#nova()
	 */
	@Override
	public AghCid nova() throws ApplicationBusinessException {
		return this.getCidsON().nova();
	}

	/**
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param capitulo
	 * @param codigoGrupo
	 * @param siglaGrupo
	 * @param descricaoGrupo
	 * @param situacaoGrupo
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#pesquisarGruposCids(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AghCapitulosCid, java.lang.Integer, java.lang.String, java.lang.String, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	@Secure("#{s:hasPermission('grupoCid','pesquisar')}")
	public List<AghGrupoCids> pesquisarGruposCids(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, AghCapitulosCid capitulo,
			Integer codigoGrupo, String siglaGrupo, String descricaoGrupo, DominioSituacao situacaoGrupo) {
		List<AghGrupoCids> grupos = this.getGrupoCidCRUD().pesquisarGruposCids(firstResult, maxResults, orderProperty, asc, capitulo, codigoGrupo, siglaGrupo,
				descricaoGrupo, situacaoGrupo);

		for (AghGrupoCids grupoCid : grupos) {
			Hibernate.initialize(grupoCid.getRapServidor());
			if (grupoCid.getRapServidor() != null) {
				Hibernate.initialize(grupoCid.getRapServidor().getPessoaFisica());
			}
		}

		return grupos;
	}

	/**
	 * @param capitulo
	 * @param codigoGrupo
	 * @param siglaGrupo
	 * @param descricaoGrupo
	 * @param situacaoGrupo
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#pesquisarGruposCidsCount(br.gov.mec.aghu.model.AghCapitulosCid, java.lang.Integer, java.lang.String, java.lang.String, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	public Long pesquisarGruposCidsCount(AghCapitulosCid capitulo, Integer codigoGrupo, String siglaGrupo, String descricaoGrupo,
			DominioSituacao situacaoGrupo) {
		return this.getGrupoCidCRUD().pesquisarGruposCidsCount(capitulo, codigoGrupo, siglaGrupo, descricaoGrupo, situacaoGrupo);
	}

	/**
	 * @param cpcSeq
	 * @param seq
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#obterGrupoCidPorId(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Secure("#{s:hasPermission('grupoCid','pesquisar')}")
	public AghGrupoCids obterGrupoCidPorId(Integer cpcSeq, Integer seq) {
		AghGrupoCids gruposCid = this.getGrupoCidCRUD().obterGrupoCidPorId(cpcSeq, seq);
		Hibernate.initialize(gruposCid.getRapServidor());
		if (gruposCid.getRapServidor() != null) {
			Hibernate.initialize(gruposCid.getRapServidor().getPessoaFisica());
		}
		return gruposCid;
	}

	/**
	 * @param aghGrupoCid
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#persistirGrupoCid(br.gov.mec.aghu.model.AghGrupoCids)
	 */
	@Override
	
	@Secure("#{s:hasPermission('grupoCid','alterar')}")
	public void persistirGrupoCid(AghGrupoCids aghGrupoCid) throws ApplicationBusinessException {
		this.getGrupoCidCRUD().persistirGrupoCid(aghGrupoCid);
	}

	/**
	 * @param aghGrupoCid
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#removerGrupoCid(br.gov.mec.aghu.model.AghGrupoCids)
	 */
	@Override
	
	@Secure("#{s:hasPermission('grupoCid','excluir')}")
	public void removerGrupoCid(AghGrupoCids aghGrupoCid) throws ApplicationBusinessException {
		this.getGrupoCidCRUD().removerGrupoCid(aghGrupoCid);
	}

	/**
	 * @param seqCapituloCid
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#pesquisarGrupoCidPorCapituloCid(java.lang.Integer)
	 */
	@Override
	@Secure("#{s:hasPermission('grupoCid','pesquisar')}")
	public List<AghGrupoCids> pesquisarGrupoCidPorCapituloCid(Integer seqCapituloCid) {
		return this.getGrupoCidCRUD().pesquisarGrupoCidPorCapituloCid(seqCapituloCid);
	}

	/**
	 * @param paramPesquisa
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#listarPorSigla(java.lang.Object)
	 */
	@Override
	@Secure("#{s:hasPermission('grupoCid','pesquisar')}")
	public List<AghGrupoCids> listarPorSigla(Object paramPesquisa) {
		return this.getGrupoCidCRUD().listarPorSigla(paramPesquisa);
	}

	/**
	 * @param sigla
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#pesquisarGrupoCidSIGLA(java.lang.String)
	 */
	@Override
	@Secure("#{s:hasPermission('grupoCid','pesquisar')}")
	public List<AghGrupoCids> pesquisarGrupoCidSIGLA(String sigla) {
		return this.getGrupoCidCRUD().pesquisarGrupoCidSIGLA(sigla);
	}

	/**
	 * @param sigla
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#pesquisarGrupoCidSIGLAS(java.lang.String)
	 */
	@Override
	@Secure("#{s:hasPermission('grupoCid','pesquisar')}")
	public List<AghGrupoCids> pesquisarGrupoCidSIGLAS(String sigla) {
		return this.getGrupoCidCRUD().pesquisarGrupoCidSIGLAS(sigla);
	}
	
	protected CapitulosCidCRUD getCapitulosCidCRUD() {
		return capitulosCidCRUD;
	}
	
	protected CidsON getCidsON() {
		return cidsON;
	}
	
	protected GrupoCidCRUD getGrupoCidCRUD() {
		return grupoCidCRUD;
	}

}
