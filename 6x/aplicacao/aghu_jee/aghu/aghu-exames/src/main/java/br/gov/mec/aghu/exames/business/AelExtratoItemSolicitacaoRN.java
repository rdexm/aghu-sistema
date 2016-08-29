package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicHistDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelMatrizSituacaoDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.vo.AelExtratoItemSolicitacaoVO;
import br.gov.mec.aghu.model.AelExtratoItemSolicHist;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMatrizSituacao;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelExtratoItemSolicitacaoRN extends BaseBusiness {


@Inject
private AelExtratoItemSolicHistDAO aelExtratoItemSolicHistDAO;

private static final Log LOG = LogFactory.getLog(AelExtratoItemSolicitacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;

@Inject
private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IExamesFacade examesFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private AelMatrizSituacaoDAO aelMatrizSituacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6036011201314189407L;

	public enum AelExtratoItemSolicitacaoRNExceptionCode implements
			BusinessExceptionCode {
		
		AEL_00420,
		AEL_00496,
		AEL_00486;

	}
	
	/**
	 * Insere um novo registro na <br>
	 * tabela AEL_EXTRATO_ITEM_SOLICS.
	 * 
	 * @param elemento
	 * @throws BaseException 
	 */
	public void inserir(AelExtratoItemSolicitacao elemento, final boolean flush) throws BaseException {
		this.beforeInsert(elemento);
		
		this.getAelExtratoItemSolicitacaoDAO().persistir(elemento);
		if (flush){
			this.getAelExtratoItemSolicitacaoDAO().flush();
		}
		this.afterInsert(elemento);
	}

	/**
	 * ORADB TRIGGER AELT_EIS_ASI
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void afterInsert(AelExtratoItemSolicitacao elemento)
		throws BaseException {
		
		this.verificarMotivoCancelamento(elemento);
	}
	
	/**
	 * ORADB TRIGGER AELT_EIS_BRI
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	protected void beforeInsert(AelExtratoItemSolicitacao elemento)
			throws BaseException {
		
		elemento.setCriadoEm(new Date());

		//RN_EIS004 Situação deve estar ativa
		this.verificarSituacao(elemento);
		
		//RN_EIS002 - Descrição: A situacao do extrato deve ser igual a situacao do ítem que o gerou.
		this.verificarSituacaoExtrato(elemento);
		
		//RN_EIS008 Quando situação forarea executora, atualizar
		this.verificarAreaExecutora(elemento);
		
		this.enviaEmail(elemento);
	}

	/**
	 * ORADB PACKAGE AELK_EIS_RN.RN_EISP_VER_SITUACAO
	 * 
	 * Verifica se a situacao esta ativa.
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	protected void verificarSituacao(AelExtratoItemSolicitacao elemento) throws ApplicationBusinessException {
		final AelSitItemSolicitacoes sitItemSolicitacao = this
				.getAelSitItemSolicitacoesDAO().obterPeloId(
						elemento.getAelSitItemSolicitacoes().getCodigo());

		if (!DominioSituacao.A.equals(sitItemSolicitacao.getIndSituacao())) {
			throw new ApplicationBusinessException(AelExtratoItemSolicitacaoRNExceptionCode.AEL_00420);
		}
	}
	
	/**
	 * ORADB PACKAGE AELK_EIS_RN.RN_EISP_VER_ITEM
	 * 
	 * A situacao do extrato deve ser igual a situacao<br> 
	 * do item que o gerou.
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	protected void verificarSituacaoExtrato(AelExtratoItemSolicitacao elemento) throws ApplicationBusinessException {
		
		final AelItemSolicitacaoExames solicitacaoExames = elemento.getItemSolicitacaoExame();
		
		if(solicitacaoExames.getSituacaoItemSolicitacao() != null && CoreUtil.modificados(elemento.getAelSitItemSolicitacoes().getCodigo(), 
				solicitacaoExames.getSituacaoItemSolicitacao().getCodigo())) {
			
			throw new ApplicationBusinessException(AelExtratoItemSolicitacaoRNExceptionCode.AEL_00496);
		}
	}

	/**
	 * ORADB PACKAGE AELK_EIS_RN.AELK_EIS_RN.RN_EISP_ATU_DTHR_EVT
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	protected void verificarAreaExecutora(AelExtratoItemSolicitacao elemento) throws ApplicationBusinessException {
		AghParametros parametroAreaExecutora = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		
		//TODO - Falta implementar aelk_eis_rn.rn_eisc_get_dt_ae
		if(StringUtils.equals(
				elemento.getAelSitItemSolicitacoes().getCodigo(), parametroAreaExecutora.getVlrTexto())) {
			
			elemento.setDataHoraEvento(new Date());
		}
	}

	/**
	 * ORADB PACKAGE AGHP_ENVIA_EMAIL (RN56)
	 * 
	 * @param {AelExtratoItemSolicitacao} elemento
	 */
	protected void enviaEmail(AelExtratoItemSolicitacao elemento) {
		
		//TODO Não deve ser implementado agora
		
		/* if trunc(:new.dthr_evento) <> trunc(:new.criado_em) then
		if :new.sit_codigo <> 'AG' then
		 v_assunto := 'Data:'||TO_CHAR(SYSDATE,'DD/MM/YY HH24:MI')||'Usu:'||user||'soe_seq:'||:new.ise_soe_seq||
		'ise_seqp:'||:new.ise_seqp||'seqp:'||:new.seqp||'SIT:'||:new.sit_codigo||'dt_eve:'||to_char(:new.dthr_evento,'dd/mm/yy hh24:mi')||
		'criado_em:'||to_char(:new.criado_em,'dd/mm/yy hh24:mi');
		 v_mensagem := 'Data:'||TO_CHAR(SYSDATE,'DD/MM/YY HH24:MI')||'Usu:'||user||'soe_seq:'||:new.ise_soe_seq||
		'ise_seqp:'||:new.ise_seqp||'seqp:'||:new.seqp||'SIT:'||:new.sit_codigo||'dt_eve:'||to_char(:new.dthr_evento,'dd/mm/yy hh24:mi')||
		'criado_em:'||to_char(:new.criado_em,'dd/mm/yy hh24:mi');
		AGHP_ENVIA_EMAIL('T',V_MENSAGEM,V_ASSUNTO,'RFOSSATI@HCPA.UFRGS.BR') ;
		end if;
		 */
		
	}
	
	/**
	 * ORADB PACKAGE AELK_EIS_RN.RN_EISP_VER_MOT_CANC
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	protected void verificarMotivoCancelamento(AelExtratoItemSolicitacao elemento) throws BaseException {
		AghParametros parametroSituacaoCancelado = null;
		
		try {
			parametroSituacaoCancelado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
		} catch(ApplicationBusinessException e) {
			throw new IllegalArgumentException("Parametro P_SITUACAO_CANCELADO nao encontrado.");
		}
		
		if (StringUtils.equals(elemento.getAelSitItemSolicitacoes().getCodigo(), parametroSituacaoCancelado.getVlrTexto())
				&& (elemento.getAelMotivoCancelaExames() == null || elemento.getAelMotivoCancelaExames().getSeq() == null)
				) {
			
			List<AelExtratoItemSolicitacao> extratoItemSolicList = this.getAelExtratoItemSolicitacaoDAO().buscaAelExtratoItemSolicitacaoAnteriores(
							elemento.getId().getIseSoeSeq(),
							elemento.getId().getIseSeqp(),
							elemento.getId().getSeqp()
			);
			
			if (extratoItemSolicList != null && !extratoItemSolicList.isEmpty()) {
				String situacao = null;
				AelExtratoItemSolicitacao element = extratoItemSolicList.get(0);
				situacao = element.getAelSitItemSolicitacoes().getCodigo();
				
				AelMatrizSituacao matrizSituacao = this.getAelMatrizSituacaoDAO().obterPorTransicao(
						situacao, 
						elemento.getAelSitItemSolicitacoes().getCodigo()
				);
				
				if (matrizSituacao != null && matrizSituacao.getIndExigeMotivoCanc()) {
					throw new ApplicationBusinessException(AelExtratoItemSolicitacaoRNExceptionCode.AEL_00486);
				}
			}
		}
	}
	
	public List<AelExtratoItemSolicitacaoVO> pesquisarAelExtratoItemSolicitacaoVOPorItemSolicitacao(Integer iseSoeSeq, Short iseSeqp, Boolean isHist) {
		List<AelExtratoItemSolicitacaoVO> listaVO;
		if(isHist){
			listaVO = pesquisarAelExtratoItemSolicitacaoVOPorItemSolicitacaoHistorico(
					iseSoeSeq, iseSeqp);
		}else{
			listaVO = pesquisarAelExtratoItemSolicitacaoVOPorItemSolicitacaoProducao(
					iseSoeSeq, iseSeqp);
		}
		
		return listaVO;
	}

	private List<AelExtratoItemSolicitacaoVO> pesquisarAelExtratoItemSolicitacaoVOPorItemSolicitacaoHistorico(
			Integer iseSoeSeq, Short iseSeqp) {
		List<AelExtratoItemSolicHist> list = getAelExtratoItemSolicHistDAO().pesquisarAelExtratoItemSolicitacaoPorItemSolicitacao(iseSoeSeq, iseSeqp);
		List<AelExtratoItemSolicitacaoVO> listaVO = null;
		
		if(list!=null && !list.isEmpty()){
			
			listaVO = new LinkedList<AelExtratoItemSolicitacaoVO>();
			for (AelExtratoItemSolicHist extrato : list) {

				AelExtratoItemSolicitacaoVO vo = new AelExtratoItemSolicitacaoVO();
				
				vo.setIseSoeSeq(extrato.getId().getIseSoeSeq());
				vo.setIseSeqp(extrato.getId().getIseSeqp());
				vo.setSeqp(extrato.getId().getSeqp());
				vo.setCriadoEm(extrato.getCriadoEm());
				vo.setSituacao(extrato.getAelSitItemSolicitacoes().getDescricao());
				RapServidores servidorExtrato = extrato.getServidor();
				vo.setServidor(servidorExtrato.getPessoaFisica().getNome());
				
				if (extrato.getServidorEhResponsabilide() != null){
						 
					final Integer matricula = extrato.getServidorEhResponsabilide().getId().getMatricula();
					final Short vinCodigo = extrato.getServidorEhResponsabilide().getId().getVinCodigo();
					
					RapServidores servidorEhResponsabilidade = null;
					if(matricula != null && vinCodigo != null){
						servidorEhResponsabilidade = getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(new RapServidoresId(matricula, vinCodigo));
					}					
					final RapQualificacao qualificacao = getRegistroColaboradorFacade().obterRapQualificacaoPorServidor(servidorEhResponsabilidade);
				
					if (qualificacao != null && qualificacao.getTipoQualificacao().getConselhoProfissional() != null && qualificacao.getTipoQualificacao().getConselhoProfissional().getSigla() != null) {
						
						vo.setSiglaConselho(qualificacao.getTipoQualificacao().getConselhoProfissional().getSigla());
						vo.setNroRegConselho(qualificacao.getNroRegConselho());
						
					}
					
					vo.setServidorResponsavel(servidorEhResponsabilidade.getPessoaFisica().getNome());				
					vo.setNomeConselho(servidorEhResponsabilidade.getPessoaFisica().getNome());
					vo.setNomeUsualConselho(servidorEhResponsabilidade.getPessoaFisica().getNomeUsual());
						
				}
				
				if(extrato.getMocSeq() != null){
					AelMotivoCancelaExames motivoCan = getExamesFacade().obterMotivoCancelamentoPeloId(extrato.getMocSeq());
					vo.setMotivoCancelamento(motivoCan.getDescricao());
				}
				vo.setComplementoMotivoCancelamento(extrato.getComplementoMotCanc());
				vo.setDataHoraEvento(extrato.getDataHoraEvento());

				listaVO.add(vo);
			}
			
		}
		return listaVO;
	}

	private List<AelExtratoItemSolicitacaoVO> pesquisarAelExtratoItemSolicitacaoVOPorItemSolicitacaoProducao(
			Integer iseSoeSeq, Short iseSeqp) {
		List<AelExtratoItemSolicitacao> list = getAelExtratoItemSolicitacaoDAO().pesquisarAelExtratoItemSolicitacaoPorItemSolicitacao(iseSoeSeq, iseSeqp);
		List<AelExtratoItemSolicitacaoVO> listaVO = null;
		
		if(list!=null && !list.isEmpty()){
			
			listaVO = new LinkedList<AelExtratoItemSolicitacaoVO>();
			for (AelExtratoItemSolicitacao extrato : list) {

				AelExtratoItemSolicitacaoVO vo = new AelExtratoItemSolicitacaoVO();
				
				vo.setIseSoeSeq(extrato.getId().getIseSoeSeq());
				vo.setIseSeqp(extrato.getId().getIseSeqp());
				vo.setSeqp(extrato.getId().getSeqp());
				vo.setCriadoEm(extrato.getCriadoEm());
				vo.setSituacao(extrato.getAelSitItemSolicitacoes().getDescricao());
				vo.setDescricao(extrato.getAelSitItemSolicitacoes().getDescricao());
				vo.setServidor(extrato.getServidor().getPessoaFisica().getNome());
				
				if (extrato.getServidorEhResponsabilide() != null){
						 
					final Integer matricula = extrato.getServidorEhResponsabilide().getId().getMatricula();
					final Short vinCodigo = extrato.getServidorEhResponsabilide().getId().getVinCodigo();
					
					RapServidores servidor = null;
					if(matricula != null && vinCodigo != null){
						servidor = getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(new RapServidoresId(matricula, vinCodigo));
					}					
					final RapQualificacao qualificacao = getRegistroColaboradorFacade().obterRapQualificacaoPorServidor(servidor);
				
					if (qualificacao != null && qualificacao.getTipoQualificacao().getConselhoProfissional() != null && qualificacao.getTipoQualificacao().getConselhoProfissional().getSigla() != null) {
						
						vo.setSiglaConselho(qualificacao.getTipoQualificacao().getConselhoProfissional().getSigla());
						vo.setNroRegConselho(qualificacao.getNroRegConselho());
						
					}
					
					vo.setServidorResponsavel(extrato.getServidorEhResponsabilide().getPessoaFisica().getNome());				
					vo.setNomeConselho(extrato.getServidorEhResponsabilide().getPessoaFisica().getNome());
					vo.setNomeUsualConselho(extrato.getServidorEhResponsabilide().getPessoaFisica().getNomeUsual());
						
				}
				
				if(extrato.getAelMotivoCancelaExames() != null){
					vo.setMotivoCancelamento(extrato.getAelMotivoCancelaExames().getDescricao());
				}
				vo.setComplementoMotivoCancelamento(extrato.getComplementoMotCanc());
				vo.setDataHoraEvento(extrato.getDataHoraEvento());

				listaVO.add(vo);
			}
			
		}
		return listaVO;
	}
	
	/** GET/SET **/

	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return aelExtratoItemSolicitacaoDAO;
	}
	protected AelExtratoItemSolicHistDAO getAelExtratoItemSolicHistDAO() {
		return aelExtratoItemSolicHistDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}
	
	protected AelMatrizSituacaoDAO getAelMatrizSituacaoDAO() {
		return aelMatrizSituacaoDAO;
	}
	
}
