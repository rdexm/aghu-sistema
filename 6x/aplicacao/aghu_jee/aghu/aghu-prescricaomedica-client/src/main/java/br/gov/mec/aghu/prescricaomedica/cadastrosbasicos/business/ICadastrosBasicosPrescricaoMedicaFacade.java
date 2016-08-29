package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.AnuTipoItemDietaUnfs;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmAlergiaUsual;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.model.MpmPlanoPosAlta;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmProcedEspecialRm;
import br.gov.mec.aghu.model.MpmServRecomendacaoAlta;
import br.gov.mec.aghu.model.MpmServRecomendacaoAltaId;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.vo.AnuTipoItemDietaVO;
@Local
public interface ICadastrosBasicosPrescricaoMedicaFacade extends Serializable {

	public MpmUnidadeMedidaMedica obterUnidadeMedicaPorId(Integer fdsUmmSeq);

	public List<MpmUnidadeMedidaMedica> pesquisarUnidadesMedidaMedicaConcentracao(
			Object siglaOuDescricao);

	public void persistirTiposDieta(AnuTipoItemDieta tipoDieta,
			List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs)
			throws ApplicationBusinessException;

	public MpmMotivoAltaMedica obterMotivoAltaMedicaPeloId(Integer seq);

	public void removerMotivoAltaMedica(Short seq,
			Integer periodo) throws BaseException;

	public void persistMotivoAltaMedica(MpmMotivoAltaMedica motivoAltaMedica)
			throws ApplicationBusinessException;

	public Long pesquisarTipoItemDietaCount(Integer codigo,
			String descricao, DominioSituacao situacao);

	public List<AnuTipoItemDietaVO> pesquisarTipoItemDieta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigo, String descricao, DominioSituacao situacao);

	public void removerTipoItemDieta(final Integer seqTipoDieta,
			Integer periodo) throws BaseException;

	public Long pesquisarMotivoAltaMedicaCount(Integer codigo,
			String descricao, String sigla, DominioSituacao situacao);

	public List<MpmMotivoAltaMedica> pesquisarMotivoAltaMedica(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigo, String descricao, String sigla,
			DominioSituacao situacao);

	public Long pesquisarPlanosPosAltaCount(Integer codigoPlano,
			String descricaoPlano, DominioSituacao situacaoPlano);

	public List<MpmPlanoPosAlta> pesquisarPlanosPosAlta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigoPlano, String descricaoPlano,
			DominioSituacao situacaoPlano);

	public Long pesquisarUnidadesMedidaMedicaCount(Integer codigoUnidade,
			String descricaoUnidade, DominioSituacao situacaoUnidade);

	public List<MpmUnidadeMedidaMedica> pesquisarUnidadesMedidaMedica(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigoMedida, String descricaoMedida,
			DominioSituacao situacaoUnidadeMedidaMedica);

	public List<MpmUnidadeMedidaMedica> pesquisarUnidadesMedidaMedica(
			Object idOuDescricao);

	public MpmPlanoPosAlta obterPlanoPosAltaPeloId(Integer seq);

	public void removerPlano(final Short codigoPlanoPosAltaExclusao, Integer periodo)
			throws BaseException;

	public void removerUnidadeMedidaMedica(Integer seq,
			Integer periodo) throws BaseException;

	public void persistPlanoPosAlta(MpmPlanoPosAlta planoPosAlta)
			throws ApplicationBusinessException;

	public void persistUnidadeMedidaMedica(MpmUnidadeMedidaMedica unidade) throws ApplicationBusinessException;

	public Long pesquisarViasAdministracaoCount(String sigla,
			String descricao, DominioSituacao situacao);

	public List<AfaViaAdministracao> pesquisarViasAdministracao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String sigla, String descricao,
			DominioSituacao situacao);

	public Long pesquisarRecomendacoesUsuarioCount(RapServidores usuario);

	public List<MpmServRecomendacaoAlta> pesquisarRecomendacoesUsuario(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores usuario);

	public MpmServRecomendacaoAlta obterRecomendacaoAltaPorId(
			MpmServRecomendacaoAltaId id);

	public void persistRecomendacaoAlta(MpmServRecomendacaoAlta recomendacao)
			throws ApplicationBusinessException;

	public void removerRecomendacao(MpmServRecomendacaoAltaId id,
			Integer periodo) throws ApplicationBusinessException;

	public void persistirViasAdministracao(
			AfaViaAdministracao viaAdministracao,
			AfaViaAdministracao viaAdministracaoAux)
			throws ApplicationBusinessException;

	public void removerViaAdministracao(final String siglaViaAdministracao,
			Integer periodo) throws BaseException;

	public MpmProcedEspecialDiversos obterProcedimentoEspecialPeloId(
			Short codigo);
	
	public MpmProcedEspecialDiversos persistirProcedimentoEspecial(MpmProcedEspecialDiversos elemento, 
						List<MpmTipoModoUsoProcedimento> modosUsos, List<MpmTipoModoUsoProcedimento> modosUsosExcluidos, 
						List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRm,
						List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRmExcluidos) throws BaseException;

	public void removerProcedimentoEspecial(Short codigoProcedimento) throws BaseException;

	public List<ScoMaterial> listarMateriaisRMAutomatica(Integer gmtCodigo, String nome, int maxResults);

	public List<MpmProcedEspecialDiversos> pesquisarProcedimentoEspecial(
			Integer firstResult, Integer maxResult, String string, boolean b,
			MpmProcedEspecialDiversos elemento);

	public Long pesquisarCount(MpmProcedEspecialDiversos elemento);

	public List<MpmUnidadeMedidaMedica> listarUnidadesMedidaMedicaAtivas(
			Object parametroConsulta);

	public List<MpmUnidadeMedidaMedica> listarUnidadesMedidaMedicaAtivas();

	
	public List<MpmCuidadoUsual> pesquisarCuidadosMedicos(String parametro);

	
	public MpmUnidadeMedidaMedica obterUnidadeMedidaMedicaPorId(Integer seq);

	public void inserirFatProcedHospInternos(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, String descricao,
			DominioSituacao indSituacao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException,
			ApplicationBusinessException;

	public MpmCuidadoUsual obterCuidadoUsualPorChavePrimaria(Integer icsCduSeq);

	void updateFatProcedHospInternosSituacao(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, DominioSituacao indSituacao,
			Short euuSeq, Integer cduSeq, Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException,
			ApplicationBusinessException;
	
	void updateFatProcedHospInternosDescr(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, String descricao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException;

	void criarFatProcedHospInternos(Short euuSeq, String descricao, DominioSituacao situacao) throws ApplicationBusinessException ;
	
	List<MpmTipoModoUsoProcedimento> buscarModosUsoPorProcedimentoEspecial(Short procedimentoEspecialId);

	List<ScoMaterial> listarMateriaisRMAutomatica(Integer gmtCodigo, String nome);
	
	public List<MpmAlergiaUsual> pesquisarAlergiaUsual(Integer codigo, String descricao, DominioSituacao situacao);
	public void salvarAlergiaUsual(MpmAlergiaUsual obj, boolean situacao) throws ApplicationBusinessException ;
	public void alterarAlergiaUsual(MpmAlergiaUsual obj, boolean situacao) throws ApplicationBusinessException ;
	void removerAlergiaUsual(MpmAlergiaUsual alergiaUsual) throws ApplicationBusinessException ;
	
	//------
	
	Integer pesquisarTipoDietaCount(AghUnidadesFuncionais unidadeFuncional);

	List<AnuTipoItemDietaUnfs> pesquisarTipoDieta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghUnidadesFuncionais unidadeFuncional);

	void excluir(AnuTipoItemDietaUnfs anuTipoItemDietaUnfs);

	void inserirTodosTiposDietasUnfs(RapServidores servidor) throws ApplicationBusinessException;

	void incluirTiposDietasUnfs(AghUnidadesFuncionais unidadeFuncional, RapServidores servidor) throws ApplicationBusinessException;

	public void persistirTiposDieta(AnuTipoItemDieta tipoDieta,
			List<AnuTipoItemDietaUnfs> listaUnidadeFuncAdicionadas,
			List<AnuTipoItemDietaUnfs> listaExcluiUnidadeFunc) throws ApplicationBusinessException;
	
}