package br.gov.mec.aghu.exames.patologia.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioConvenioExameSituacao;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSecaoConfiguravel;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSimNaoNaoAplicavel;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.patologia.vo.AelItemSolicitacaoExameLaudoUnicoVO;
import br.gov.mec.aghu.exames.patologia.vo.AelKitMatPatologiaVO;
import br.gov.mec.aghu.exames.patologia.vo.AelPatologistaLaudoVO;
import br.gov.mec.aghu.exames.patologia.vo.ConsultaConfigExamesVO;
import br.gov.mec.aghu.exames.patologia.vo.ConsultaItensPatologiaVO;
import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.exames.vo.AelIdentificarGuicheVO;
import br.gov.mec.aghu.exames.vo.AelpCabecalhoLaudoVO;
import br.gov.mec.aghu.exames.vo.MedicoSolicitanteVO;
import br.gov.mec.aghu.exames.vo.RelatorioLaudoUnicoVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaLaminasVO;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelCadGuiche;
import br.gov.mec.aghu.model.AelCestoPatologia;
import br.gov.mec.aghu.model.AelCidos;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelDescMatLacunas;
import br.gov.mec.aghu.model.AelDescMatLacunasId;
import br.gov.mec.aghu.model.AelDescMaterialAps;
import br.gov.mec.aghu.model.AelDiagnosticoAps;
import br.gov.mec.aghu.model.AelDiagnosticoLaudos;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExtratoExameAp;
import br.gov.mec.aghu.model.AelExtratoExameApId;
import br.gov.mec.aghu.model.AelGrpDescMatLacunas;
import br.gov.mec.aghu.model.AelGrpDescMatLacunasId;
import br.gov.mec.aghu.model.AelGrpDiagLacunas;
import br.gov.mec.aghu.model.AelGrpDiagLacunasId;
import br.gov.mec.aghu.model.AelGrpMacroLacuna;
import br.gov.mec.aghu.model.AelGrpMacroLacunaId;
import br.gov.mec.aghu.model.AelGrpMicroLacuna;
import br.gov.mec.aghu.model.AelGrpMicroLacunaId;
import br.gov.mec.aghu.model.AelGrpTxtDescMats;
import br.gov.mec.aghu.model.AelGrpTxtPadraoDiags;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMacro;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMicro;
import br.gov.mec.aghu.model.AelIndiceBlocoAp;
import br.gov.mec.aghu.model.AelInformacaoClinicaAP;
import br.gov.mec.aghu.model.AelItemConfigExame;
import br.gov.mec.aghu.model.AelItemConfigExameId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelKitIndiceBloco;
import br.gov.mec.aghu.model.AelKitItemIndiceBloco;
import br.gov.mec.aghu.model.AelKitItemIndiceBlocoId;
import br.gov.mec.aghu.model.AelKitItemMatPatologia;
import br.gov.mec.aghu.model.AelKitItemMatPatologiaId;
import br.gov.mec.aghu.model.AelKitMatPatologia;
import br.gov.mec.aghu.model.AelLaminaAps;
import br.gov.mec.aghu.model.AelMacroscopiaAps;
import br.gov.mec.aghu.model.AelMarcador;
import br.gov.mec.aghu.model.AelMaterialAp;
import br.gov.mec.aghu.model.AelMovimentoGuiche;
import br.gov.mec.aghu.model.AelNomenclaturaAps;
import br.gov.mec.aghu.model.AelNomenclaturaEspecs;
import br.gov.mec.aghu.model.AelNomenclaturaEspecsId;
import br.gov.mec.aghu.model.AelNomenclaturaGenerics;
import br.gov.mec.aghu.model.AelNotaAdicionalAp;
import br.gov.mec.aghu.model.AelOcorrenciaExameAp;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelPatologistaAps;
import br.gov.mec.aghu.model.AelTextoPadraoColoracs;
import br.gov.mec.aghu.model.AelTextoPadraoDiags;
import br.gov.mec.aghu.model.AelTextoPadraoDiagsId;
import br.gov.mec.aghu.model.AelTextoPadraoMacro;
import br.gov.mec.aghu.model.AelTextoPadraoMacroId;
import br.gov.mec.aghu.model.AelTextoPadraoMicro;
import br.gov.mec.aghu.model.AelTextoPadraoMicroId;
import br.gov.mec.aghu.model.AelTopografiaAparelhos;
import br.gov.mec.aghu.model.AelTopografiaAparelhosId;
import br.gov.mec.aghu.model.AelTopografiaAps;
import br.gov.mec.aghu.model.AelTopografiaCidOs;
import br.gov.mec.aghu.model.AelTopografiaGrupoCidOs;
import br.gov.mec.aghu.model.AelTopografiaLaudos;
import br.gov.mec.aghu.model.AelTopografiaSistemas;
import br.gov.mec.aghu.model.AelTxtDescMats;
import br.gov.mec.aghu.model.AelTxtDescMatsId;
import br.gov.mec.aghu.model.AelTxtDiagLacunas;
import br.gov.mec.aghu.model.AelTxtDiagLacunasId;
import br.gov.mec.aghu.model.AelTxtMacroLacuna;
import br.gov.mec.aghu.model.AelTxtMacroLacunaId;
import br.gov.mec.aghu.model.AelTxtMicroLacuna;
import br.gov.mec.aghu.model.AelTxtMicroLacunaId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAelApXPatologiaAghu;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IExamesPatologiaFacade extends Serializable {

	AelTextoPadraoColoracs obterAelTextoPadraoColoracs(final Integer seq);

	void persistirAelExameAp(final AelExameAp aelExameAp) throws BaseException;

	List<AelTextoPadraoColoracs> pesquisarAelTextoPadraoColoracs(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final AelTextoPadraoColoracs aelTextoPadraoColoracs);

	Long pesquisarAelTextoPadraoColoracsCount(final AelTextoPadraoColoracs aelTextoPadraoColoracs);

	List<AelTextoPadraoColoracs> pesquisarAelTextoPadraoColoracs(final String filtro, final DominioSituacao situacao);

	Long pesquisarAelTextoPadraoColoracsCount(final String filtro, final DominioSituacao situacao);

	List<AelTextoPadraoColoracs> listarAelTextoPadraoColoracs(final DominioSituacao situacao);

	AelTextoPadraoColoracs obterTextoPadraoColoracsPelaDescricaoExata(String filtro);

	void inserirAelTextoPadraoColoracs(final AelTextoPadraoColoracs aelTextoPadraoColoracs) throws ApplicationBusinessException;

	void alterarAelTextoPadraoColoracs(final AelTextoPadraoColoracs aelTextoPadraoColoracs) throws BaseException;

	void excluirAelTextoPadraoColoracs(final Integer seq) throws ApplicationBusinessException;

	Long pesquisarAelCadGuicheCount(final AelCadGuiche aelCadGuiche);

	List<AelIdentificarGuicheVO> pesquisarAelCadGuiche(final Integer firstResult, final Integer maxResults, final String orderProperty,
			final boolean asc, final AelCadGuiche aelCadGuiche);

	AelCadGuiche obterAelCadGuiche(final Short seq);

	AelCadGuiche obterAelCadGuichePorUsuarioUnidadeExecutora(final AghUnidadesFuncionais unidade, final String usuario,
			final DominioSituacao situacao, Short seqGuiche);

	void inserirAelCadGuiche(final AelCadGuiche aelCadGuiche) throws ApplicationBusinessException, ApplicationBusinessException;

	void alterarAelCadGuiche(final AelCadGuiche aelCadGuiche, String nomeMicrocomputado) throws ApplicationBusinessException,
			ApplicationBusinessException;

	void excluirAelCadGuiche(final Short aelCadGuiche) throws ApplicationBusinessException;

	AelMovimentoGuiche criarAelMovimentoGuiche(final Short cguSeq, String nomeMicrocomputador) throws ApplicationBusinessException;

	List<AelGrpTxtPadraoDiags> pesquisarAelGrpTxtPadraoDiags(final Short seq, final String descricao, final DominioSituacao situacao);

	List<AelGrpTxtPadraoDiags> pesquisarAelGrpTxtPadraoDiags(final String filtro, final DominioSituacao situacao);

	Long pesquisarAelGrpTxtPadraoDiagsCount(final String filtro, final DominioSituacao situacao);

	AelGrpTxtPadraoDiags obterAelGrpTxtPadraoDiags(final Short seq);

	AelTextoPadraoDiags obterAelTextoPadraoDiags(final AelTextoPadraoDiagsId aelTextoPadraoDiagsId);

	AelGrpDiagLacunas obterAelGrpDiagLacunas(final AelGrpDiagLacunasId aelGrpDiagLacunasId);

	AelTxtDiagLacunas obterAelTxtDiagLacunas(final AelTxtDiagLacunasId aelTxtDiagLacunasId);

	List<AelGrpTxtPadraoMacro> pesquisarGrupoTextoPadraoMacro(final Short codigo, final String descricao, final DominioSituacao situacao);

	List<AelGrpTxtPadraoMacro> pesquisarGrupoTextoPadraoMacro(final String filtro, final DominioSituacao situacao);

	Long pesquisarGrupoTextoPadraoMacroCount(final String filtro, final DominioSituacao situacao);

	AelGrpTxtPadraoMacro obterAelGrpTxtPadraoMacro(final Short seq);

	void alterarAelGrpTxtPadraoMacro(final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro) throws BaseException;

	void excluirAelGrpTxtPadraoMacro(final Short seq) throws BaseException;

	void inserirAelGrpTxtPadraoMacro(final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro) throws BaseException;

	List<AelTextoPadraoMacro> pesquisarTextoPadraoMacroPorAelGrpTxtPadraoMacro(final Short seqAelGrpTxtPadraoMacro);

	List<AelTextoPadraoMacro> pesquisarTextoPadraoMacroscopia(final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacros, final String filtro,
			final DominioSituacao indSituacao);

	Long pesquisarTextoPadraoMacroscopiaCount(final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacros, final String filtro,
			final DominioSituacao indSituacao);

	void alterarAelTextoPadraoMacro(final AelTextoPadraoMacro aelTextoPadraoMacro) throws BaseException;

	void inserirAelTextoPadraoMacro(final AelTextoPadraoMacro aelTextoPadraoMacro) throws BaseException;

	void excluirAelTextoPadraoMacro(final AelTextoPadraoMacroId id) throws BaseException;

	AelTextoPadraoMacro obterAelTextoPadraoMacro(final AelTextoPadraoMacroId aelTextoPadraoMacroId);

	List<AelGrpMacroLacuna> pesquisarAelGrpMacroLacunaPorTextoPadraoMacro(final Short aelTextoPadraoMacroLubSeq,
			final Short aelTextoPadraoMacroSeqp, final DominioSituacao indSituacao);

	void alterarAelGrpMacroLacuna(final AelGrpMacroLacuna aelGrpMacroLacuna) throws BaseException;

	void inserirAelGrpMacroLacuna(final AelGrpMacroLacuna aelGrpMacroLacuna) throws BaseException;

	void excluirAelGrpMacroLacuna(final AelGrpMacroLacunaId id) throws BaseException;

	AelGrpMacroLacuna obterAelGrpMacroLacuna(final AelGrpMacroLacunaId aelGrpMacroLacunaId);

	List<AelTxtMacroLacuna> pesquisarAelTxtMacroLacunaPorAelGrpMacroLacuna(final AelGrpMacroLacuna aelGrpMacroLacuna, final DominioSituacao indSituacao);

	void alterarAelTxtMacroLacuna(final AelTxtMacroLacuna aelTxtMacroLacuna) throws BaseException;

	void inserirAelTxtMacroLacuna(final AelTxtMacroLacuna aelTxtMacroLacuna) throws BaseException;

	AelTxtMacroLacuna obterAelTxtMacroLacuna(final AelTxtMacroLacunaId aelTxtMacroLacunaId);

	void excluirAelTxtMacroLacuna(final AelTxtMacroLacunaId id) throws BaseException;

	List<AelTextoPadraoDiags> pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiags(final Short seqAelGrpTxtPadraoDiag);

	List<AelTextoPadraoDiags> pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiags(final Short seqAelGrpTxtPadraoDiag,
			final String filtro, final DominioSituacao situacao);

	Long pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiagsCount(final Short seqAelGrpTxtPadraoDiag, final String filtro,
			final DominioSituacao situacao);

	void persistirAelGrpTxtPadraoDiags(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags) throws BaseException;

	void excluirAelGrpTxtPadraoDiags(final Short seq) throws BaseException;

	List<AelGrpTxtPadraoMicro> pesquisarGrupoTextoPadraoMicro(final Short seq, final String descricao, final DominioSituacao situacao);

	AelGrpTxtPadraoMicro obterAelGrpTxtPadraoMicro(final Short seq);

	void inserirAelGrpTxtPadraoMicro(final AelGrpTxtPadraoMicro aelGrpTxtPadraoMicro) throws BaseException;

	void excluirAelGrpTxtPadraoMicro(final Short seq) throws BaseException;

	void alterarAelGrpTxtPadraoMicro(final AelGrpTxtPadraoMicro aelGrpTxtPadraoMicro) throws BaseException;

	List<AelTextoPadraoMicro> pesquisarTextoPadraoMicroPorAelGrpTxtPadraoMicro(final Short seqAelGrpTxtPadraoMicro);

	AelTextoPadraoMicro obterAelTextoPadraoMicro(final AelTextoPadraoMicroId aelTextoPadraoMicroId);

	void alterarAelTextoPadraoMicro(final AelTextoPadraoMicro aelTextoPadraoMicro) throws BaseException;

	void inserirAelTextoPadraoMicro(final AelTextoPadraoMicro aelTextoPadraoMicro) throws BaseException;

	void excluirAelTextoPadraoMicro(final AelTextoPadraoMicroId id) throws BaseException;

	List<AelGrpMicroLacuna> pesquisarAelGrpMicroLacunaPorTextoPadraoMicro(final Short aelTextoPadraoMicroLubSeq,
			final Short aelTextoPadraoMicroSeqp);

	AelGrpMicroLacuna obterAelGrpMicroLacuna(final AelGrpMicroLacunaId aelGrpMicroLacunaId);

	void alterarAelGrpMicroLacuna(final AelGrpMicroLacuna aelGrpMicroLacuna) throws BaseException;

	void inserirAelGrpMicroLacuna(final AelGrpMicroLacuna aelGrpMicroLacuna) throws BaseException;

	void excluirAelGrpMicroLacuna(final AelGrpMicroLacunaId id) throws BaseException;

	List<AelTxtMicroLacuna> pesquisarAelTxtMicroLacunaPorAelGrpMicroLacuna(final AelGrpMicroLacuna aelGrpMicroLacuna);

	AelTxtMicroLacuna obterAelTxtMicroLacuna(final AelTxtMicroLacunaId aelTxtMicroLacunaId);

	void alterarAelTxtMicroLacuna(final AelTxtMicroLacuna aelTxtMicroLacuna) throws BaseException;

	void inserirAelTxtMicroLacuna(final AelTxtMicroLacuna aelTxtMicroLacuna) throws BaseException;

	void excluirAelTxtMicroLacuna(final AelTxtMicroLacuna aelTxtMicroLacuna) throws BaseException;

	List<AelPatologista> listarPatologistas(final Integer firstResult, final Integer maxResult, final Integer seq, final String nome,
			final DominioFuncaoPatologista funcao, final Boolean permiteLibLaudo, final DominioSituacao situacao,
			final RapServidoresId servidor, final String nomeParaLaudo);

	void persistirIdentificacaoGuiche(final AelCadGuiche guiche, final AghUnidadesFuncionais unidade, final DominioSituacao situacao,
			String nomeMicrocomputador) throws BaseException;

	Long listarPatologistasCount(final Integer seq, final String nome, final DominioFuncaoPatologista funcao,
			final Boolean permiteLibLaudo, final DominioSituacao situacao, final RapServidoresId servidor, final String nomeParaLaudo);

	AelPatologista obterPatologistaPorChavePrimaria(final Integer seq);

	AelPatologista obterAelPatologistaAtivoPorServidorEFuncao(final RapServidores servidor, final DominioFuncaoPatologista... funcao);

	List<AelPatologista> listarPatologistasPorCodigoNomeFuncao(final String filtro, final DominioFuncaoPatologista... funcao);

	Long listarPatologistasPorCodigoNomeFuncaoCount(final String filtro, final DominioFuncaoPatologista... funcao);

	List<AelPatologista> listarPatologistasAtivosPorCodigoNomeFuncao(final String filtro, final DominioFuncaoPatologista... funcao);

	Long listarPatologistasAtivosPorCodigoNomeFuncaoCount(final String filtro, final DominioFuncaoPatologista... funcao);

	AelPatologista clonarPatologista(final AelPatologista patologista) throws BaseException;

	List<AelConfigExLaudoUnico> listarConfigExames(ConsultaConfigExamesVO consulta);

	Long listarConfigExamesCount(ConsultaConfigExamesVO consulta);

	List<AelItemConfigExame> listarItemConfigExames(final Integer firstResult, final Integer maxResult, final Integer lu2Seq);

	Long listarItemConfigExamesCount(final Integer lu2Seq);

	List<AelExamesMaterialAnalise> listarExamesMaterialAnaliseUnfExecExames(final Object objPesquisa, final Short unfSeq);

	AelConfigExLaudoUnico obterConfigExameLaudoUncioPorChavePrimaria(final Integer seq);

	AelConfigExLaudoUnico persistirConfigLaudoUnico(AelConfigExLaudoUnico config) throws BaseException;

	void excluirConfigLaudo(final Integer config) throws BaseException;

	AelItemConfigExame clonarItemConfigExame(final AelItemConfigExame item) throws BaseException;

	void persistirItemConfigExame(final AelItemConfigExame item, final AelItemConfigExame itemOld, final Boolean inclusao)
			throws BaseException;

	void excluirItemConfigExame(final AelItemConfigExame item, final AelItemConfigExame itemOld) throws BaseException;

	AelItemConfigExame obterItemConfigExame(final AelItemConfigExameId id);

	void persistirAelTextoPadraoDiags(final AelTextoPadraoDiags aelTextoPadraoDiags) throws BaseException;

	void excluirAelTextoPadraoDiags(final AelTextoPadraoDiagsId id) throws BaseException;

	void persistirAelGrpDiagLacunas(final AelGrpDiagLacunas aelGrpDiagLacunas) throws BaseException;

	void excluirAelGrpDiagLacunas(final AelGrpDiagLacunasId id) throws BaseException;

	List<AelGrpDiagLacunas> pesquisarAelGrpDiagLacunasPorTextoPadraoDiags(final Short aelTextoPadraoDiagsLuhSeq,
			final Short aelTextoPadraoDiagsSeqp, final DominioSituacao indSituacao);

	void persistirAelTxtDiagLacunas(final AelTxtDiagLacunas aelTxtDiagLacunas) throws BaseException;

	List<AelTxtDiagLacunas> pesquisarAelTxtDiagLacunasPorAelGrpDiagLacunas(final AelGrpDiagLacunas aelGrpDiagLacunas, final DominioSituacao indSituacao);

	void excluirAelTxtDiagLacunas(final AelTxtDiagLacunas aelTxtDiagLacunas) throws BaseException;

	List<AelPatologista> pesquisarPatologistas(final Object valor);

	List<AelPatologista> pesquisarPatologistasPorFuncao(final Object valor, final DominioFuncaoPatologista[] funcao);

	List<AelConfigExLaudoUnico> pesquisarAelConfigExLaudoUnico(final String orderProperty, final String filtro);

	Long pesquisarAelConfigExLaudoUnicoCount(final String orderProperty, final String filtro);

	List<AelPatologista> pesquisarPatologistasPorNomeESituacao(final Object parametro, final DominioSituacao indSituacao, final Integer max);

	List<VAelApXPatologiaAghu> pesquisarVAelApXPatologiaAghu(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final AelPatologista residenteResp,
			final DominioSituacaoExamePatologia situacaoExmAnd, final Date dtDe, final Date dtAte, final AelPatologista patologistaResp,
			final AelConfigExLaudoUnico exame, final Long numeroAp, final MedicoSolicitanteVO medicoSolic, final AelExameAp material,
			final DominioConvenioExameSituacao convenio, final DominioSimNao laudoImpresso) throws BaseException;

	List<VAelApXPatologiaAghu> pesquisarVAelApXPatologiaAghu(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final AelPatologista residenteResp,
			final DominioSituacaoExamePatologia situacaoExmAnd, final Date dtDe, final Date dtAte, final AelPatologista patologistaResp,
			final AelConfigExLaudoUnico exame, final Long numeroAp) throws BaseException;

	List<VAelApXPatologiaAghu> pesquisarVAelApXPatologiaAghu(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final AelNomenclaturaGenerics nomenclaturaGenerica,
			final AelNomenclaturaEspecs nomenclaturaEspecifica, final AelTopografiaSistemas topografiaSistema,
			final AelTopografiaAparelhos topografiaAparelho, final AelPatologista residenteResp, final AelPatologista patologistaResp,
			final DominioSimNao neoplasiaMaligna, final DominioSimNaoNaoAplicavel margemComprometida, final DominioSimNao biopsia, 
			final Date dtDe, final Date dtAte, final AelConfigExLaudoUnico exame) throws BaseException;

	Integer pesquisarVAelApXPatologiaAghuCount(final AelPatologista residenteResp, final DominioSituacaoExamePatologia situacaoExmAnd,
			final Date dtDe, final Date dtAte, final AelPatologista patologistaResp, final AelConfigExLaudoUnico exame,
			final Long numeroAp, final MedicoSolicitanteVO medicoSolic, final AelExameAp material,
			final DominioConvenioExameSituacao convenio, final DominioSimNao laudoImpresso);

	Integer pesquisarVAelApXPatologiaAghuCount(final AelPatologista residenteResp, final DominioSituacaoExamePatologia situacaoExmAnd,
			final Date dtDe, final Date dtAte, final AelPatologista patologistaResp, final AelConfigExLaudoUnico exame,
			final Long numeroAp);

	Integer pesquisarVAelApXPatologiaAghuCount(final AelNomenclaturaGenerics nomenclaturaGenerica,
			final AelNomenclaturaEspecs nomenclaturaEspecifica, final AelTopografiaSistemas topografiaSistema,
			final AelTopografiaAparelhos topografiaAparelho, final AelPatologista residenteResp, final AelPatologista patologistaResp,
			final DominioSimNao neoplasiaMaligna, final DominioSimNaoNaoAplicavel margemComprometida, final DominioSimNao biopsia, 
			final Date dtDe, final Date dtAte, final AelConfigExLaudoUnico exame);

	List<MedicoSolicitanteVO> pesquisarMedicosSolicitantesVO(final String filtro, final String siglaConselhoProfissional);

	Integer pesquisarMedicosSolicitantesVOCount(final String filtro, final String siglaConselhoProfissional);

	List<AelExameAp> pesquisarAelExameApPorMateriais(final String filtro);

	Long pesquisarAelExameApPorMateriaisCount(final String filtro);

	AelAnatomoPatologico obterAelAnatomoPatologicoByNumeroAp(final Long numeroAp, final Integer configExame);

	AelAnatomoPatologico obterAelAnatomoPatologicoPorItemSolic(Integer soeSeq, Short seqp);

	AelAnatomoPatologico obterAelAnatomoPatologico(final Long seq);

	AelApXPatologista obterAelApXPatologistaPorSeqAnatoPatologicoMatriculaEFuncao(final Long lumSeq, final Integer matricula,
			final DominioFuncaoPatologista[] funcao, final DominioSituacao situacao);

	void persistirAelApXPatologista(final AelApXPatologista apXPatologista) throws BaseException;

	void excluirAelApXPatologistaPorPatologista(final Integer seqExcluir, final Long seq) throws ApplicationBusinessException;

	ConvenioExamesLaudosVO aelcBuscaConvLaud(final Integer atdSeq, final Integer atvSeq);

	DominioSimNao aelcBuscaConvGrp(final Integer atdSeq, final Integer atvSeq);

	List<AelNomenclaturaGenerics> pesquisarAelNomenclaturaGenerics(final String filtro, final DominioSituacao situacao);

	Long pesquisarAelNomenclaturaGenericsCount(final String filtro, final DominioSituacao situacao);

	List<AelNomenclaturaGenerics> pesquisarAelNomenclaturaGenerics(final Integer seq, final String filtro, final DominioSituacao situacao);

	AelNomenclaturaGenerics obterAelNomenclaturaGenericsPorChavePrimaria(final Integer seq);

	void alterarAelNomenclaturaGenerics(final AelNomenclaturaGenerics aelNomenclaturaGenerics) throws BaseException;

	void excluirAelNomenclaturaGenerics(final AelNomenclaturaGenerics aelNomenclaturaGenerics) throws BaseException;

	void inserirAelNomenclaturaGenerics(final AelNomenclaturaGenerics aelNomenclaturaGenerics) throws BaseException;

	List<AelNomenclaturaEspecs> pesquisarAelNomenclaturaEspecs(final String filtro, final DominioSituacao situacao,
			final AelNomenclaturaGenerics aelNomenclaturaGenerics);

	List<AelNomenclaturaEspecs> pesquisarAelNomenclaturaEspecsPorAelNomenclaturaGenerics(
			final AelNomenclaturaGenerics aelNomenclaturaGenerics);

	AelNomenclaturaEspecs obterAelNomenclaturaEspecsPorChavePrimaria(final AelNomenclaturaEspecsId aelNomenclaturaEspecsId);

	Long pesquisarAelNomenclaturaEspecsCount(final String filtro, final DominioSituacao situacao,
			final AelNomenclaturaGenerics aelNomenclaturaGenerics);

	void alterarAelNomenclaturaEspecs(final AelNomenclaturaEspecs aelNomenclaturaEspecs) throws BaseException;

	void excluirAelNomenclaturaEspecs(final AelNomenclaturaEspecs aelNomenclaturaEspecs) throws BaseException;

	void inserirAelNomenclaturaEspecs(final AelNomenclaturaEspecs aelNomenclaturaEspecs) throws BaseException;

	List<AelTopografiaSistemas> pesquisarAelTopografiaSistemas(final String filtro, final DominioSituacao situacao);

	Long pesquisarAelTopografiaSistemasCount(final String filtro, final DominioSituacao situacao);

	List<AelTopografiaSistemas> pesquisarAelTopografiaSistemas(final Integer seq, final String filtro, final DominioSituacao situacao);

	AelTopografiaSistemas obterAelTopografiaSistemasPorChavePrimaria(final Integer seq);

	void alterarAelTopografiaSistemas(final AelTopografiaSistemas aelTopografiaSistemas) throws BaseException;

	void excluirAelTopografiaSistemas(final Integer seq) throws BaseException;

	void inserirAelTopografiaSistemas(final AelTopografiaSistemas aelTopografiaSistemas) throws BaseException;

	List<AelTopografiaAparelhos> pesquisarAelTopografiaAparelhos(final String filtro, final DominioSituacao situacao,
			final AelTopografiaSistemas aelTopografiaSistemas);

	Long pesquisarAelTopografiaAparelhosCount(final String filtro, final DominioSituacao situacao,
			final AelTopografiaSistemas aelTopografiaSistemas);

	List<AelTopografiaAparelhos> pesquisarAelTopografiaAparelhos(final AelTopografiaSistemas aelTopografiaSistemas);

	AelTopografiaAparelhos obterAelTopografiaAparelhosPorChavePrimaria(final AelTopografiaAparelhosId id);

	void alterarAelTopografiaAparelhos(final AelTopografiaAparelhos aelTopografiaAparelhos) throws BaseException;

	void excluirAelTopografiaAparelhos(final AelTopografiaAparelhosId id) throws BaseException;

	void inserirAelTopografiaAparelhos(final AelTopografiaAparelhos aelTopografiaAparelhos) throws BaseException;

	List<AelKitIndiceBloco> pesquisarAelKitIndiceBloco(final Integer seq, final String descricao, final DominioSituacao situacao);

	Long pesquisarAelKitIndiceBlocoCount(final Integer seq, final String filtro, final DominioSituacao situacao);

	AelKitIndiceBloco obterAelKitIndiceBlocoPorChavePrimaria(final Integer seq);

	void alterarAelKitIndiceBloco(final AelKitIndiceBloco aelKitIndiceBloco) throws BaseException;

	void excluirAelKitIndiceBloco(final Integer aelKitIndiceBloco) throws BaseException;

	void inserirAelKitIndiceBloco(final AelKitIndiceBloco aelKitItemIndiceBloco) throws BaseException;

	List<AelKitItemIndiceBloco> pesquisarAelKitItemIndiceBloco(final AelKitIndiceBloco aelKitIndiceBloco);

	AelKitItemIndiceBloco obterAelKitItemIndiceBlocoPorChavePrimaria(final AelKitItemIndiceBlocoId id);

	void alterarAelKitItemIndiceBloco(final AelKitItemIndiceBloco aelKitItemIndiceBloco) throws BaseException;

	void excluirAelKitItemIndiceBloco(final AelKitItemIndiceBlocoId id) throws BaseException;

	void inserirAelKitItemIndiceBloco(final AelKitItemIndiceBloco aelKitItemIndiceBloco) throws BaseException;

	List<AelKitMatPatologia> pesquisarAelKitMatPatologia(final Integer seq, final String descricao, final DominioSituacao situacao);

	AelKitMatPatologia obterAelKitMatPatologiaPorChavePrimaria(final Integer seq);

	void alterarAelKitMatPatologia(final AelKitMatPatologia aelKitMatPatologia) throws BaseException;

	void excluirAelKitMatPatologia(final Integer seq) throws BaseException;

	void inserirAelKitMatPatologia(final AelKitMatPatologia aelKitMatPatologia) throws BaseException;

	List<AelKitItemMatPatologia> pesquisarAelKitItemMatPatologia(final AelKitMatPatologia aelKitMatPatologia);

	AelKitItemMatPatologia obterAelKitItemMatPatologiaPorChavePrimaria(final AelKitItemMatPatologiaId id);

	void alterarAelKitItemMatPatologia(final AelKitItemMatPatologia aelKitItemMatPatologia) throws BaseException;

	void excluirAelKitItemMatPatologia(final AelKitItemMatPatologiaId id) throws BaseException;

	void inserirAelKitItemMatPatologia(final AelKitItemMatPatologia aelKitItemMatPatologia) throws BaseException;

	AelExameAp obterAelExameApPorChavePrimaria(final Long luxSeq);

	AelExameAp obterAelExameApPorAelAnatomoPatologicos(final AelAnatomoPatologico aelAP);

	List<AelCestoPatologia> pesquisarAelCestoPatologia(final Integer seq, final String descricao, final String sigla,
			final DominioSituacao situacao);

	List<AelCestoPatologia> pesquisarAelCestoPatologia(final String filtro, final DominioSituacao situacao);

	Long pesquisarAelCestoPatologiaCount(final String filtro, final DominioSituacao situacao);

	AelCestoPatologia obterAelCestoPatologiaPorChavePrimaria(final Integer seq);

	void alterarAelCestoPatologia(final AelCestoPatologia aelCestoPatologia) throws BaseException;

	void inserirAelCestoPatologia(final AelCestoPatologia aelCestoPatologia) throws BaseException;

	void persistirAelMaterialAp(final AelMaterialAp aelMaterialAp) throws BaseException;

	void excluirAelMaterialAp(final AelMaterialAp aelMaterialAp) throws BaseException;

	List<RelatorioMapaLaminasVO> pesquisarRelatorioMapaLaminasVO(final Date dtRelatorio, final AelCestoPatologia cesto);

	Date obterMaxCriadoEmPorLuxSeqEEtapasLaudo(final Long luxSeq, final DominioSituacaoExamePatologia etapasLaudo);

	List<AelExtratoExameAp> listarAelExtratoExameApPorLuxSeq(final Long luxSeq);

	AelMacroscopiaAps obterAelMacroscopiaApsPorAelExameAps(final Long luxSeq);

	void concluirMacroscopiaAps(final AelExameAp exameAp, final AelMacroscopiaAps macroscopia) throws BaseException;

	void validarMacroscopiaPreenchida(final AelMacroscopiaAps macroscopia) throws ApplicationBusinessException;

	void persistirAelMacroscopiaAps(final AelMacroscopiaAps macroscopia) throws BaseException;

	void persistirAelDiagnosticoAps(final AelDiagnosticoAps diagnosticoAps) throws BaseException;

	void concluirDiagnosticoAps(final AelExameAp exameAp, final AelDiagnosticoAps diagnostico,
			final List<AelTopografiaAps> listaTopografiaAp, final List<AelNomenclaturaAps> listaNomenclaturaAp,
			final List<AelLaminaAps> listaLaminaAp) throws BaseException;

	AelDiagnosticoAps obterAelDiagnosticoApsPorAelExameAps(final Long luxSeq);

	List<RelatorioLaudoUnicoVO> obterRelatorioLaudoUnicoVO(final String nroAps, final AelConfigExLaudoUnico aelConfigExLaudoUnico) throws BaseException;

	AelpCabecalhoLaudoVO obterAelpCabecalhoLaudo(final Short unfSeq) throws ApplicationBusinessException;

	List<AelMaterialAp> obterAelMaterialApPorAelExameAps(final Long luxSeq);

	AelMacroscopiaAps obterAelMacroscopiaApPorChavePrimaria(final Long chavePrimaria);

	AelDiagnosticoAps obterAelDiagnosticoApPorChavePrimaria(final Long chavePrimaria);

	void aelpAtualizaTela(final TelaLaudoUnicoVO telaVo) throws BaseException;

	AelItemSolicitacaoExameLaudoUnicoVO obterAelItemSolicitacaoExameLaudoUnicoVO(final AelExameAp aelExameAp, final boolean isPesqSolic);

	List<AelKitMatPatologiaVO> listaMateriais(final Long luxSeq, DominioSituacaoExamePatologia etapaLaudo);

	void atualizaMateriaisVO(List<AelKitMatPatologiaVO> listaVO);

	List<AelMaterialAp> listarAelMaterialApPorLuxSeqEOrdem(final Long luxSeq, Short ordem);

	List<AelMaterialAp> listarAelMaterialApPorLuxSeqEOrdemMaior(final Long luxSeq, Short ordem);

	List<AelNotaAdicionalAp> obterListaNotasAdicionaisPeloExameApSeq(Long luxSeq);

	void persistirAelNotaAdicionalAp(final AelNotaAdicionalAp aelNotaAdicionalAp) throws BaseException;

	Boolean assinarReabrirLaudo(Long numeroAp, Long luxSeq, Integer lu2Seq, DominioSituacaoExamePatologia etapasLaudo,
			AelDiagnosticoAps diagnostico, List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException;

	Boolean assinarLaudo(Long numeroAp, Long luxSeq, Integer lu2Seq, AelDiagnosticoAps diagnostico,
			List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException;

	Boolean reabrirLaudo(Long numeroAp, Long luxSeq, Integer lu2Seq, DominioSituacaoExamePatologia etapasLaudo,
			AelDiagnosticoAps diagnostico, List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException;

	List<AelTopografiaAps> listarTopografiaApPorLuxSeq(Long luxSeq);

	List<AelNomenclaturaAps> listarNomenclaturaApPorLuxSeq(Long luxSeq);

	List<AelPatologistaLaudoVO> listarPatologistaLaudo(Long luxSeq);
	
	AelPatologista obterPatologistaPorServidor(final RapServidores servidor);

	List<AelPatologistaAps> listarPatologistaLaudoPorLuxSeqEOrdem(final Long luxSeq, Short ordem);

	List<AelPatologistaAps> listarPatologistaLaudoPorLuxSeqEOrdemMaior(final Long luxSeq, Short ordem);

	void persistirAelPatologistaAps(final AelPatologistaAps aelPatologistaAp) throws BaseException;

	void persistir(final AelTopografiaAps topografiaAps) throws BaseException;

	void excluir(final AelTopografiaAps topografiaAps) throws BaseException;

	void persistir(final AelNomenclaturaAps nomenclaturaAps) throws BaseException;

	void excluir(final AelNomenclaturaAps nomenclaturaAps) throws BaseException;

	AelTopografiaAps obterAelTopografiaApsPorChavePrimaria(Long seq);

	AelNomenclaturaAps obterAelNomenclaturaApsPorChavePrimaria(Long seq);

	void carregaPatologistas(TelaLaudoUnicoVO telaVo) throws BaseException;

	void atualizaMateriais(AelExameAp aelExameAp) throws BaseException;

	void atualizaInformacoesClinicas(TelaLaudoUnicoVO telaVo) throws BaseException;

	AelPatologistaAps obterAelPatologistaApsPorChavePrimaria(Integer seq);

	void excluir(final AelPatologistaAps patologistaAps) throws BaseException;

	AelInformacaoClinicaAP obterAelInformacaoClinicaApPorAelExameAps(final Long luxSeq);

	void persistirAelInformacaoClinicaAP(final AelInformacaoClinicaAP aelInformacaoClinicaAP) throws BaseException;

	void persistir(final AelIndiceBlocoAp aelIndiceBlocoAp) throws BaseException;

	List<AelIndiceBlocoAp> listarAelIndiceBlocoApPorAelExameAps(final Long luxSeq);

	AelIndiceBlocoAp obterAelIndiceBlocoApPorChavePrimaria(final Long seq);

	void excluir(final AelIndiceBlocoAp aelIndiceBlocoAp) throws BaseException;

	List<AelLaminaAps> obterListaLaminasPeloExameApSeq(Long luxSeq);

	AelLaminaAps obterLaminasPelaChavePrimaria(Long seq);

	void persistirAelLaminaAps(final AelLaminaAps aelLaminaAps) throws BaseException;

	void excluirAelLaminaAps(final AelLaminaAps aelLaminaAps) throws BaseException;

	void ativarInativarImunoHistoquimica(List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException;

	List<AelOcorrenciaExameAp> buscarAelOcorrenciaExameApPorSeqExameAp(Long luxSeq);

	void atualizarImpressao(final Long luxSeq) throws BaseException;

	void validarPatologistaExcluir(RapServidores servidorPatologista, RapServidores patologistaApExcluir)
			throws ApplicationBusinessException;

	String replaceSustenidoLaudoUnico(String str, String oldValue, String newValue);

	void gravarNotasAdicionais(String notas, Long luxSeq) throws BaseException;

	public List<AelTopografiaCidOs> listarTopografiaCidOs();

	List<AelTopografiaCidOs> listarTopografiaCidOsPorGrupo(Long seqGrupo);

	List<AelTopografiaGrupoCidOs> listarTopografiaGrupoCidOsPorGrupo(Long seqGrupo);

	List<AelTopografiaGrupoCidOs> listarTopografiaGrupoCidOsNodosRaiz();

	AelTopografiaCidOs obterCidOPorChavePrimaria(Long cidOSeq);

	// /////// cdi-O //////////////////////////////////////////////
	public List<AelTopografiaCidOs> listarTopografiaCidOs(Object pesquisa);

	public Long listarTopografiaCidOsCount(Object pesquisa);

	public AelTopografiaLaudos obterAelTopografiaLaudosPorChavePrimaria(final Long seq);

	public List<AelTopografiaLaudos> listarTopografiaLaudosPorSeqExame(Long seqExame);

	public void persistirTopografiaLaudos(final AelTopografiaLaudos topografiaLaudos) throws BaseException;

	public void excluirTopografiaLaudos(final AelTopografiaLaudos topografiaLaudos) throws BaseException;

	public List<AelDiagnosticoLaudos> listarDiagnosticoLaudosPorSeqExame(Long seqExame);

	public void persistirDiagnosticoLaudos(final AelDiagnosticoLaudos diagnosticoLaudos) throws BaseException;

	public AelDiagnosticoLaudos obterAelDiagnosticoLaudosPorChavePrimaria(final Long seq);

	public void excluirDiagnosticoLaudos(final AelDiagnosticoLaudos diagnosticoLaudos) throws BaseException;

	public AelTopografiaLaudos obterTopografiaLaudos(Long seqExame, String codigo);

	public List<AelCidos> listarAelCidos(Object param);

	public Long listarAelCidosCount(Object param);

	// Manter Texto Padrão de Descrição de Materiais

	List<AelGrpTxtDescMats> pesquisarGrupoTextoPadraoDescMats(Short codigo, String descricao, DominioSituacao situacao);

	List<AelGrpTxtDescMats> pesquisarGrupoTextoPadraoDescMats(String filtro, DominioSituacao situacao);

	Long pesquisarGrupoTextoPadraoDescMatsCount(String filtro, DominioSituacao situacao);

	AelGrpTxtDescMats obterAelGrpTxtDescMats(Short seq);

	void alterarAelGrpTxtDescMats(AelGrpTxtDescMats AelGrpTxtDescMats) throws BaseException;

	void excluirAelGrpTxtDescMats(AelGrpTxtDescMats AelGrpTxtDescMats) throws BaseException;

	void inserirAelGrpTxtDescMats(AelGrpTxtDescMats AelGrpTxtDescMats) throws BaseException;

	List<AelTxtDescMats> pesquisarTextoPadraoDescMatsPorAelGrpTxtDescMats(Short seqAelGrpTxtDescMats);

	List<AelTxtDescMats> pesquisarTextoPadraoDescMats(AelGrpTxtDescMats AelGrpTxtDescMats, String filtro, DominioSituacao indSituacao);

	Long pesquisarTextoPadraoDescMatsCount(AelGrpTxtDescMats AelGrpTxtDescMats, String filtro, DominioSituacao indSituacao);

	void alterarAelTxtDescMats(AelTxtDescMats AelTxtDescMats) throws BaseException;

	void inserirAelTxtDescMats(AelTxtDescMats AelTxtDescMats) throws BaseException;

	void excluirAelTxtDescMats(AelTxtDescMats AelTxtDescMats) throws BaseException;

	AelTxtDescMats obterAelTxtDescMats(AelTxtDescMatsId AelTxtDescMatsId);

	List<AelGrpDescMatLacunas> pesquisarAelGrpDescMatLacunasPorTextoPadraoDescMats(Short AelTxtDescMatsGtmSeq, Short AelTxtDescMatsSeqp, final DominioSituacao indSituacao);

	void alterarAelGrpDescMatLacunas(AelGrpDescMatLacunas AelGrpDescMatLacunas) throws BaseException;

	void inserirAelGrpDescMatLacunas(AelGrpDescMatLacunas AelGrpDescMatLacunas) throws BaseException;

	void excluirAelGrpDescMatLacunas(AelGrpDescMatLacunas AelGrpDescMatLacunas) throws BaseException;

	AelGrpDescMatLacunas obterAelGrpDescMatLacunas(AelGrpDescMatLacunasId AelGrpDescMatLacunasId);

	List<AelDescMatLacunas> pesquisarAelDescMatLacunasPorAelGrpDescMatLacunas(AelGrpDescMatLacunas AelGrpDescMatLacunas, final DominioSituacao indSituacao);

	void alterarAelDescMatLacunas(AelDescMatLacunas AelDescMatLacunas) throws BaseException;

	void inserirAelDescMatLacunas(AelDescMatLacunas AelDescMatLacunas) throws BaseException;

	AelDescMatLacunas obterAelDescMatLacunas(AelDescMatLacunasId AelDescMatLacunasId);

	void excluirAelDescMatLacunas(AelDescMatLacunas AelDescMatLacunas) throws BaseException;

	void concluirDescMaterialAps(AelExameAp exameAp, AelDescMaterialAps macroscopia) throws BaseException;

	void validarDescMaterialPreenchida(AelDescMaterialAps macroscopia) throws ApplicationBusinessException;

	void persistirAelDescMaterialAps(AelDescMaterialAps macroscopia) throws BaseException;

	AelDescMaterialAps obterAelDescMaterialApsPorAelExameAps(Long luxSeq);

	AelDescMaterialAps obterAelDescMaterialApPorChavePrimaria(Long chavePrimaria);

	/*
	 * #21881
	 */
	AelMarcador obterAelMarcadorPorChavePrimaria(final Integer seq);

	List<AelMarcador> pesquisarAelMarcador(final Integer firstResult, final Integer maxResults, final String orderProperty,
			final boolean asc, final AelMarcador aelMarcador);

	Long pesquisarAelMarcadorCount(final AelMarcador aelMarcador);

	void inserirAelMarcador(final AelMarcador aelMarcador) throws BaseException;

	void alterarAelMarcador(final AelMarcador aelMarcador) throws BaseException;

	String ativarInativarAelMarcador(AelMarcador aelMarcadorEdicao);

	/**
	 * Busca o IND_OBRIGATORIO de uma seção configurável ativa, filtrando pela
	 * descrição e pela versão de configuração e pelo id da configuração de
	 * exames de laudo único.
	 * 
	 * C1 da #21585
	 * 
	 * @param secao
	 * @param versaoConf
	 * @param lu2Seq
	 * @return
	 */
	Boolean buscarAtivoPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel secao, Integer versaoConf, Integer lu2Seq);

	void validaSigla(String sigla) throws ApplicationBusinessException;

	void persisteSecaoConfigurcaoExames(AelConfigExLaudoUnico configExame);

	Short obterMaxSeqAelExtratoExameAp(Long luxSeq, DominioSituacaoExamePatologia etapasLaudo);

	AelExtratoExameAp obterAelExtratoExameApPorChavePrimaria(AelExtratoExameApId id);

	void aelpAtualizaAel(Integer lu2Seq, Long luxSeq, String nomeMicrocomputador) throws BaseException;

	void atualizaMateriais(final AelExameAp aelExameAp, final AelItemSolicitacaoExamesId aelItemSolicitacaoExamesId) throws BaseException;

	List<AelMaterialAp> pesquisaMateriaisCapsula(Object value, Long luxSeq);

	Long pesquisaMateriaisCapsulaCount(Object value, Long luxSeq);

	void gravarLaminas(List<AelLaminaAps> laminasEmMemoria, List<AelLaminaAps> laminasExcluir, AelExameAp exame) throws BaseException;
	
	AelConfigExLaudoUnico obterPorSigla(String sigla);
	
	List<ConsultaItensPatologiaVO> listaExamesComVersaoLaudo(Long luxSeq, Integer calcSeq, String[] sitCodigo);
	
	void excluir(final AelOcorrenciaExameAp aelOcorrenciaExameAp, String usuarioLogado) throws BaseException;
	
	void excluir(final AelInformacaoClinicaAP aelInformacaoClinicaAP, String usuarioLogado) throws BaseException;
	
	void excluir(final AelExameAp aelExameAp, String usuarioLogado) throws BaseException;
	
	void excluirAelApXPatologista(final AelApXPatologista aelApXPatologista) throws ApplicationBusinessException;
	
	void excluir(final AelAnatomoPatologico aelAnatomoPatologico, String usuarioLogado) throws BaseException;
	
	AelConfigExLaudoUnico obterConfigExLaudoUnico(AelItemSolicitacaoExames item);

	void removeAdicionaPatologistaLaudo(AelAnatomoPatologico anatomoPatologico, AelPatologista antigoPatologistaResp, AelPatologista novoPatologistaResp, RapServidores servidorLogado) throws BaseException;	

	void persistirPatologista(AelPatologista patologista) throws BaseException;

	void excluirPatologista(Integer seq) throws BaseException;

	AelPatologista obterPatologista(Integer seq);

	Long pesquisarAelConfigExLaudoUnicoCount(String filtro);

	AelExameAp obterAelExameApPorSeq(Long luxSeq);
	
	AelMaterialAp obterAelMaterialApPorSeq(Long luxSeq);

	void persistirAelMaterialAp(AelMaterialAp materialAp,
			AelMaterialAp materialAPOriginalOld, Boolean isInsert) throws BaseException;

	void persistirAelMaterialAp(AelMaterialAp aelMaterialAp, AelMaterialAp aelMaterialApOld) throws BaseException;

	void persistirAelExameAp(AelExameAp aelExameAp, AelExameAp aelExameApOld) throws BaseException;
	
	AelAnatomoPatologico obterAelAnatomoPatologicoPorId(Long seq);

	AelCadGuiche obterAelGuiche(Short seq);

}