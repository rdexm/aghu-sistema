package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.farmacia.dao.AfaDispMdtoCbSpsDAO;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaPrescricaoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class AfaDispensacaoMdtosRN extends BaseBusiness
		implements Serializable {
	
	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final Log LOG = LogFactory.getLog(AfaDispensacaoMdtosRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;
	
	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private AfaDispMdtoCbSpsDAO afaDispMdtoCbSpsDAO;
	
	@Inject
	private AfaPrescricaoMedicamentoDAO afaPrescricaoMedicamentoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4575035016674717750L;

	public enum AfaDispensacaoMdtosRNExceptionCode implements	BusinessExceptionCode {
		AFA_00260, AFA_00253, AFA_00298, AFA_00299, AFA_00284, AFA_00285, MPM_00778, 
		MPM_00938, AFA_00286, AFA_00445, AFA_01493, AFA_01494, AFA_00446, AFA_00447, 
		AFA_01234, AFA_01473, AFA_00169, AFA_00444, AFA_00258, MPM_03088, MPM_01574, 
		MPM_03097, MPM_03090, MPM_01708, MPM_01573, PROBLEMA_EXECUTAR_OPERACAO,
		BLOQUEIO_ALTERACAO_DISPENSACAO_COM_ETIQUETA, BLOQUEIO_ALTERACAO_QUANTIDADE_DISPENSADA
	}
	
	private Boolean achouDisp;
	private Long dsmSeq;
	
	/**
	 * @ORADB Trigger AGH.AFAT_DSM_BRU
	 * Os atributos: Medicamento, Servidor,ServidorConferida,
	 * ServidorEntregue,ServidorEstornado,ServidorTriadoPor;
	 * da entidade admOld devem ser acessadas via LAZY antes de chamar este método.
	 * @param admNew --> Entidade nova, que sofreu as modificações
	 * @param admOld --> Entidade Original antes das modificações
	 * @throws BaseException 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void atualizaAfaDispMdto(AfaDispensacaoMdtos admNew, AfaDispensacaoMdtos admOld, String nomeMicrocomputador) throws BaseException{
		admNew.setIndexItemSendoAtualizado(Boolean.TRUE);
		validarAlteracaoQtdeESituacaoDaDispensacao(admNew, admOld);
		
		if(!CoreUtil.igual(admOld.getMedicamento(), admNew.getMedicamento())
				|| !CoreUtil.igual(getServidorIdNotNull(admOld.getServidor(), true),  getServidorIdNotNull(admNew.getServidor(), true))
				|| !CoreUtil.igual(getServidorIdNotNull(admOld.getServidor(), false), getServidorIdNotNull(admNew.getServidor(), false))
				|| !CoreUtil.igual(getServidorIdNotNull(admOld.getServidorEntregue(), true),  getServidorIdNotNull(admNew.getServidorEntregue(), true))
				|| !CoreUtil.igual(getServidorIdNotNull(admOld.getServidorEntregue(), false), getServidorIdNotNull(admNew.getServidorEntregue(), false))
				|| !CoreUtil.igual(admOld.getCriadoEm(), admNew.getCriadoEm())
				|| !CoreUtil.igual(admOld.getQtdeDispensada(), admNew.getQtdeDispensada())
				|| !CoreUtil.igual(admOld.getIndSituacao(), admNew.getIndSituacao())
				){
			rnDsmpVerAltera(admOld.getIndSituacao());
		}
		
		if(!CoreUtil.igual(admOld.getQtdeDispensada(), admNew.getQtdeDispensada()) 
				&& /*##33115*/!DominioSituacaoItemPrescritoDispensacaoMdto.DT.equals(admNew.getIndSitItemPrescrito())){

			admNew.setIndSituacao(rnDsmpVerAltQtde(admNew.getSeq(), admOld.getQtdeDispensada(), admNew.getQtdeDispensada(), admNew.getIndSituacao()));
		}
		
		if((DominioSituacaoDispensacaoMdto.E.equals(admNew.getIndSituacao())  && !DominioSituacaoDispensacaoMdto.E.equals(admOld.getIndSituacao()))
				|| !CoreUtil.igual(getServidorIdNotNull(admOld.getServidorEntregue(), true),  getServidorIdNotNull(admNew.getServidorEntregue(), true))
				|| !CoreUtil.igual(getServidorIdNotNull(admOld.getServidorEntregue(), false), getServidorIdNotNull(admNew.getServidorEntregue(), false))
				){
			rnDsmpVerSerBusc(admNew.getServidorEntregue());
		}
		
		/* Para sit <> 'S' não permite alterar qtde_dispensada, tod_seq e unf_seq */
		if(!DominioSituacaoDispensacaoMdto.S.equals(admNew.getIndSituacao())
				&& !DominioSituacaoDispensacaoMdto.D.equals(admNew.getIndSituacao())
				&& !DominioSituacaoDispensacaoMdto.T.equals(admNew.getIndSituacao())
				&& (!CoreUtil.igual(admNew.getQtdeDispensada(), admOld.getQtdeDispensada())
				|| !CoreUtil.igual(admNew.getTipoOcorrenciaDispensacao(), admOld.getTipoOcorrenciaDispensacao())
				|| !CoreUtil.igual(admNew.getUnidadeFuncional(), admOld.getUnidadeFuncional()))
				){
			rnDsmpVerSitAlt();
		}
		
		if(!CoreUtil.igual(admNew.getIndSituacao(), admOld.getIndSituacao())){
			if(
				   (DominioSituacaoDispensacaoMdto.S.equals(admOld.getIndSituacao()) && DominioSituacaoDispensacaoMdto.D.equals(admNew.getIndSituacao()))
				|| (DominioSituacaoDispensacaoMdto.D.equals(admOld.getIndSituacao()) && DominioSituacaoDispensacaoMdto.C.equals(admNew.getIndSituacao()))
				|| (DominioSituacaoDispensacaoMdto.C.equals(admOld.getIndSituacao()) && DominioSituacaoDispensacaoMdto.E.equals(admNew.getIndSituacao()))
				|| (DominioSituacaoDispensacaoMdto.E.equals(admOld.getIndSituacao()) && DominioSituacaoDispensacaoMdto.C.equals(admNew.getIndSituacao()))
				|| (DominioSituacaoDispensacaoMdto.C.equals(admOld.getIndSituacao()) && DominioSituacaoDispensacaoMdto.D.equals(admNew.getIndSituacao()))
				|| (DominioSituacaoDispensacaoMdto.D.equals(admOld.getIndSituacao()) && DominioSituacaoDispensacaoMdto.S.equals(admNew.getIndSituacao()))
				|| (DominioSituacaoDispensacaoMdto.S.equals(admOld.getIndSituacao()) && DominioSituacaoDispensacaoMdto.T.equals(admNew.getIndSituacao()))
				|| (DominioSituacaoDispensacaoMdto.D.equals(admOld.getIndSituacao()) && DominioSituacaoDispensacaoMdto.T.equals(admNew.getIndSituacao()))
				|| (DominioSituacaoDispensacaoMdto.T.equals(admOld.getIndSituacao()) && DominioSituacaoDispensacaoMdto.S.equals(admNew.getIndSituacao()))
				|| (DominioSituacaoDispensacaoMdto.T.equals(admOld.getIndSituacao()) && DominioSituacaoDispensacaoMdto.D.equals(admNew.getIndSituacao()))
			){
				if(admNew.getIndSituacao().equals(DominioSituacaoDispensacaoMdto.T)){
					admNew.setServidor(null);
					admNew.setDthrDispensacao(null);
				}
			}else{
				rnDsmpVerAltSit();
			}
		}
		
		/* Verifica se tipo  ocorrência dispensação está ativa */
		if(admNew.getTipoOcorrenciaDispensacao() !=null && !CoreUtil.igual(admNew.getTipoOcorrenciaDispensacao(), admOld.getTipoOcorrenciaDispensacao())){
			rnDsmpVerTodAtiv(admNew.getTipoOcorrenciaDispensacao());
		}
		
		/* Verifica se unid. funcional é farmácia e se está ativa */
		if(!CoreUtil.igual(admNew.getUnidadeFuncional(), admOld.getUnidadeFuncional())){
			rnDsmpVerUnfDisp(admNew.getUnidadeFuncional());
		}
		
		/* dados do servidor que digita */
		if(admNew.getIndSituacao().equals(DominioSituacaoDispensacaoMdto.S)){
			admNew.setServidor(null);
			admNew.setDthrDispensacao(null);
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		/* dados do servidor que recebeu */
		if(DominioSituacaoDispensacaoMdto.D.equals(admNew.getIndSituacao())){
			if(DominioSituacaoDispensacaoMdto.S.equals(admOld.getIndSituacao())
				|| !CoreUtil.igual(admNew.getQtdeDispensada(), admOld.getQtdeDispensada())
				|| !CoreUtil.igual(admNew.getTipoOcorrenciaDispensacao(), admOld.getTipoOcorrenciaDispensacao())){
//				if (admNew.getServidor() == null){
					admNew.setServidor(servidorLogado);
//				}
				admNew.setDthrDispensacao(new Date());
			}
			admNew.setServidorConferida(null);
			admNew.setDthrConferencia(null);
			
			if(((admNew.getServidor() == null) || (admNew.getServidor().getId() == null)
				||(admNew.getServidor().getId().getMatricula() == null))
				&& DominioSituacaoDispensacaoMdto.S.equals(admOld.getIndSituacao())) {
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_00445);
			}
		}
		
		if(DominioSituacaoDispensacaoMdto.T.equals(admNew.getIndSituacao())){
			if(DominioSituacaoDispensacaoMdto.S.equals(admOld.getIndSituacao())
				|| !CoreUtil.igual(admOld.getQtdeDispensada(), admNew.getQtdeDispensada())
				|| !CoreUtil.igual(admOld.getTipoOcorrenciaDispensacao(), admNew.getTipoOcorrenciaDispensacao())){
				
				admNew.setServidorTriadoPor(servidorLogado);
				admNew.setDthrTriado(new Date());
			}
			if (admNew.getServidorTriadoPor() == null
					|| admNew.getServidorTriadoPor().getId() == null
					|| admNew.getServidorTriadoPor().getId().getMatricula() == null) {
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_01493);
			}
		}
		
		if(DominioSituacaoDispensacaoMdto.D.equals(admNew.getIndSituacao())){
			if (DominioSituacaoDispensacaoMdto.T.equals(admOld.getIndSituacao())
					|| !CoreUtil.igual(admOld.getQtdeDispensada(),admNew.getQtdeDispensada())
					|| !CoreUtil.igual(admOld.getTipoOcorrenciaDispensacao(),admNew.getTipoOcorrenciaDispensacao())) {
				
				admNew.setServidor(servidorLogado);
				admNew.setDthrDispensacao(new Date());
			}
			if((admNew.getServidor() == null || admNew.getServidor().getId() == null ||admNew.getServidor().getId().getMatricula() == null)
					&& DominioSituacaoDispensacaoMdto.T.equals(admOld.getIndSituacao())){
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_01494);
			}
		}
		
		/* dados do servidor que conferiu */
		if(DominioSituacaoDispensacaoMdto.C.equals(admNew.getIndSituacao())){
			if (DominioSituacaoDispensacaoMdto.D.equals(admOld.getIndSituacao())) {
				admNew.setServidorConferida(servidorLogado);
				admNew.setDthrConferencia(new Date());
			}
				admNew.setServidorEntregue(null);
				admNew.setDthrEntrega(null);
			
			if ((admNew.getServidorConferida() == null
					|| admNew.getServidorConferida().getId() == null 
					|| admNew.getServidorConferida().getId().getMatricula() == null)){
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_00446);
			}
		}
		
		/* dados do servidor que envia */
		if(DominioSituacaoDispensacaoMdto.E.equals(admNew.getIndSituacao())){
			if (DominioSituacaoDispensacaoMdto.C.equals(admOld.getIndSituacao())) {
				admNew.setServidorEntregue(servidorLogado);
				admNew.setDthrEntrega(new Date());
			}
			if((admNew.getServidorEntregue() == null || admNew.getServidorEntregue().getId().getMatricula() == null)){
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_00447);
			}
		}
		
		/* atualiza a máquina (micro) onde foi gerada a dispensação de medicamentos */
		if(!CoreUtil.igual(admOld.getTipoOcorrenciaDispensacao(), admNew.getTipoOcorrenciaDispensacao())){
			if(DominioSituacaoDispensacaoMdto.S.equals(admOld.getIndSituacao()) 
				&& DominioSituacaoDispensacaoMdto.D.equals(admNew.getIndSituacao())){
				admNew.setNomeEstacaoDisp(nomeMicrocomputador);
			}
			if(DominioSituacaoDispensacaoMdto.S.equals(admNew.getIndSituacao())){
				admNew.setNomeEstacaoDisp(null);
			}
		}
		
		if(!CoreUtil.igual(admOld.getTipoOcorrenciaDispensacao(), admNew.getTipoOcorrenciaDispensacao())){
			if(DominioSituacaoDispensacaoMdto.T.equals(admOld.getIndSituacao()) 
				&& DominioSituacaoDispensacaoMdto.D.equals(admNew.getIndSituacao())){
				admNew.setNomeEstacaoDisp(nomeMicrocomputador);
			}
			if(DominioSituacaoDispensacaoMdto.S.equals(admNew.getIndSituacao())){
				admNew.setNomeEstacaoDisp(null);
			}
		}
		
		/* Atualiza dados do estorno de medicamentos (dthr e servidor de estorno) */
		if(!CoreUtil.igual(admOld.getQtdeEstornada(), admNew.getQtdeEstornada())
			||!CoreUtil.igual(admOld.getTipoOcorrenciaDispensacaoEstornado(), admNew.getTipoOcorrenciaDispensacaoEstornado()) ){
			if(admNew.getQtdeEstornada() == null){
				admNew.setServidorEstornado(null);
				admNew.setDthrEstorno(null);
			}else{
				if (CoreUtil.notIn(admNew.getIndSituacao(),
						DominioSituacaoDispensacaoMdto.C,
						DominioSituacaoDispensacaoMdto.D,
						DominioSituacaoDispensacaoMdto.E,
						DominioSituacaoDispensacaoMdto.T)) {
					throw new ApplicationBusinessException(
							AfaDispensacaoMdtosRNExceptionCode.AFA_01234);
				}
				
				admNew.setServidorEstornado(servidorLogado);
				admNew.setDthrEstorno(new Date());
				
				if(admNew.getServidorEstornado() == null || admNew.getServidorEstornado().getId().getMatricula() == null){
					throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_00447);
				}
			}
		}
		
		getAfaDispensacaoMdtosDAO().atualizar(admNew);
		admNew.setIndexItemSendoAtualizado(Boolean.FALSE);
	}
	
	/**
	 * Melhoria #19063
	 * 
	 * @param admNew
	 * @param admOld
	 * @throws ApplicationBusinessException
	 */
	private void validarAlteracaoQtdeESituacaoDaDispensacao(
			AfaDispensacaoMdtos admNew, AfaDispensacaoMdtos admOld)
			throws ApplicationBusinessException {
		if (!CoreUtil.igual(admNew.getIndSituacao(), admOld.getIndSituacao())
				&& DominioSituacaoDispensacaoMdto.T.equals(admOld
						.getIndSituacao())
				&& DominioSituacaoDispensacaoMdto.S.equals(admNew
						.getIndSituacao())) {
			Long rQtdeEtiq = getAfaDispMdtoCbSpsDAO()
					.getDispMdtoCbSpsExcluidoByDispensacaoMdtoCount(
							admNew.getSeq(), null);
			if (rQtdeEtiq != 0) {
				throw new ApplicationBusinessException(
						AfaDispensacaoMdtosRNExceptionCode.BLOQUEIO_ALTERACAO_DISPENSACAO_COM_ETIQUETA);
			}
		} else {
			if (!CoreUtil.igual(admNew.getQtdeDispensada(),
					admOld.getQtdeDispensada())
					// dispensado por checkBox = indSituacao = D e qtdEtiqueta =
					// 0
					&& ((DominioSituacaoDispensacaoMdto.D.equals(admNew
							.getIndSituacao()) && getAfaDispMdtoCbSpsDAO()
							.getDispMdtoCbSpsExcluidoByDispensacaoMdtoCount(
									admNew.getSeq(), null) == 0))
							//##33115
							&& !DominioSituacaoItemPrescritoDispensacaoMdto.DT.equals(admOld.getIndSitItemPrescrito())
							&& !DominioSituacaoItemPrescritoDispensacaoMdto.DT.equals(admNew.getIndSitItemPrescrito())) {
				throw new ApplicationBusinessException(
						AfaDispensacaoMdtosRNExceptionCode.BLOQUEIO_ALTERACAO_QUANTIDADE_DISPENSADA);
			}
		}
	}
	
	// BEFORE INSERT

	/**
	 * @ORADB Trigger "AGH".AFAT_DSM_BRI
	 * @param AfaDispensacaoMdtos admNew
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void criaDispMdtoTriagemPrescricao(AfaDispensacaoMdtos admNew, String nomeMicrocomputador)
			throws ApplicationBusinessException {
		
		admNew.setIndexItemSendoAtualizado(Boolean.TRUE);
		
		if (DominioSituacaoDispensacaoMdto.E.equals(admNew.getIndSituacao())) {
			rnDsmpVerSerBusc(admNew.getServidorEntregue());
		}
		
		/* Verifica se tipo  ocorrência dispensação está ativa */
		if (admNew.getTipoOcorrenciaDispensacao() != null) {
			rnDsmpVerTodAtiv(
					admNew.getTipoOcorrenciaDispensacao());
		}
		/* Verifica se unid. funcional é farmácia e se está ativa */
		rnDsmpVerUnfDisp(
					admNew.getUnidadeFuncional());
		
		/* Atualiza a unf_seq que solicitou o medicamento */
		if (admNew.getUnidadeFuncionalSolicitante() == null) {
			Integer atdSeq = null;
			if (admNew.getItemPrescricaoMdto().getId().getPmdAtdSeq() != null) {
				atdSeq = admNew.getItemPrescricaoMdto().getId().getPmdAtdSeq();
			} else {
				atdSeq = admNew.getImoPmoPteAtdSeq();
			}
			admNew = atualizarUnidadeSolicitante(admNew, atdSeq);
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		/* quando a dispensação já é gerada como dispensada atualizar o servidor e
		   a data e hora da dispensação */
		if (DominioSituacaoDispensacaoMdto.D.equals(admNew.getIndSituacao())) {
			if (servidorLogado == null) {
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_00444);
			}
			admNew.setServidor(servidorLogado);
			admNew.setDthrDispensacao(new Date());
		}

		/* Estacao de trabalho do usuario*/
		if ( DominioSituacaoDispensacaoMdto.D.equals(admNew.getIndSituacao()) ||
				DominioSituacaoDispensacaoMdto.C.equals(admNew.getIndSituacao()) ||
				DominioSituacaoDispensacaoMdto.E.equals(admNew.getIndSituacao()) ) {
			String estacaoUsuario = nomeMicrocomputador;
			admNew.setNomeEstacaoDisp(estacaoUsuario);
		}
			
		if (admNew.getAtendimento() == null) {
			AghAtendimentos atendimento = new AghAtendimentos();
			if (admNew.getItemPrescricaoMdto().getId().getPmdAtdSeq() != null) {
				atendimento.setSeq(admNew.getItemPrescricaoMdto().getId().getPmdAtdSeq());
			} else {
				atendimento.setSeq(admNew.getImoPmoPteAtdSeq());
			}
			admNew.setAtendimento(atendimento);
		}
		if (DominioSituacaoDispensacaoMdto.T.equals(admNew.getIndSituacao()) && 
				!DominioSituacaoItemPrescritoDispensacaoMdto.RD.equals(admNew.getIndSitItemPrescrito())) {
			if (servidorLogado == null) {
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_01493);
			}
			admNew.setServidorTriadoPor(servidorLogado);
			admNew.setDthrTriado(new Date());
		}
		
		admNew.setCriadoEm(new Date());
		admNew.setIndSitEspecial(false);
		getAfaDispensacaoMdtosDAO().persistir(admNew);
		admNew.setIndexItemSendoAtualizado(Boolean.FALSE);
		
	}
	
	/**
	 * Método que permite acessar RapServidoresId de um RapServidores, testando se RapServidores é nulo
	 * Retorna a matrícula ou vinCodigo de RapServidoresId
	 * @param servidor
	 * @param matricula
	 * @return
	 */
	private Object getServidorIdNotNull(RapServidores servidor, boolean matricula) {
		if(servidor != null && servidor.getId() !=null){
			if(matricula) {
				return servidor.getId().getMatricula();
			} else {
				return servidor.getId().getVinCodigo();
			}
		}
		return null;
	}

	/**
	 * @ORADB afak_dsm_rn.rn_dsmp_ver_unf_disp (:new.unf_seq);
	 * @param unidadeFuncional
	 */
	public void rnDsmpVerUnfDisp(AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException {
		if(unidadeFuncional == null) {
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.MPM_00938);
		} else if(!DominioSituacao.A.equals(unidadeFuncional.getIndSitUnidFunc())) {
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.MPM_00778);
		}
		if(!unfEFarmacia(unidadeFuncional)){
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_00286);
		}
	}
	
	/**
	 * Verifica se a unidade funcional possui a caracteristica Unid. Farmácia
	 * Unf é Farmácia?
	 * @param unidadeFuncional
	 * @return
	 */
	public boolean unfEFarmacia(AghUnidadesFuncionais unidadeFuncional) {
		return getAghuFacade().unidadeFuncionalPossuiCaracteristica(unidadeFuncional.getSeq(), 
			ConstanteAghCaractUnidFuncionais.UNID_FARMACIA);
	}

	/**
	 * @ORADB afak_dsm_rn.rn_dsmp_ver_tod_ativ;
	 * @param tipoOcorrenciaDispensacao
	 */
	public void rnDsmpVerTodAtiv(
			AfaTipoOcorDispensacao tipoOcorrenciaDispensacao) throws ApplicationBusinessException {
		if(tipoOcorrenciaDispensacao.getSituacao() == null) {
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_00284); 
		} else {
			if(!DominioSituacao.A.equals(tipoOcorrenciaDispensacao.getSituacao())) {
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_00285);
			}
		}
	}

	/**
	 * @ORADB afak_dsm_rn.rn_dsmp_ver_alt_sit
	 * @throws ApplicationBusinessException 
	 */
	private void rnDsmpVerAltSit() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_00299); 
	}

	/**
	 * @ORADB afak_dsm_rn.rn_dsmp_ver_sit_alt
	 * @throws ApplicationBusinessException 
	 */
	private void rnDsmpVerSitAlt() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_00298); 
	}

	/**
	 * @ORADB afak_dsm_rn.rn_dsmp_ver_ser_busc
	 * verifica se servidor responsável está ativo 
	 * @param matricula
	 * @param vinCodigo
	 * @throws ApplicationBusinessException 
	 */
	public void rnDsmpVerSerBusc(RapServidores servidorEntregue) throws ApplicationBusinessException {
		Integer matricula 	= (Integer)getServidorIdNotNull(servidorEntregue, true);
		
		getServidorIdNotNull(servidorEntregue, false);
		
		if(matricula !=null) {
			if(!rnAfacVerSerAtiv(servidorEntregue)){
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_00253); 
			}
		}
	}
	/**
	 * @ORADB afak_rn.rn_afac_ver_ser_ativ
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public boolean rnAfacVerSerAtiv(RapServidores servidor) throws ApplicationBusinessException {
		if(servidor == null) {
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_00169);
		}

		if (DominioSituacaoVinculo.A.equals(servidor.getIndSituacao())
				|| (DominioSituacaoVinculo.P.equals(servidor.getIndSituacao()) && CoreUtil.isMaiorOuIgualDatas(
						servidor.getDtFimVinculo(), new Date()))) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * @ORADB afak_dsm_rn.rn_dsmp_ver_alt_qtde
	 * @param seq
	 * @param qtdeDispensada
	 * @param qtdeDispensada2
	 * @param indSituacao
	 * @throws ApplicationBusinessException 
	 */
	public DominioSituacaoDispensacaoMdto rnDsmpVerAltQtde(Long seqAfaDispMdto, BigDecimal qtdeDispensadaOld,
			BigDecimal qtdeDispensadaNew,
			DominioSituacaoDispensacaoMdto indSituacao) throws ApplicationBusinessException {
		
		
		//Integer rQtdeEtiqLidas = getAfaDispMdtoCbSpsDAO().getDispMdtoCbSpsExcluidoByDispensacaoMdtoCount(seqAfaDispMdto, DominioIndExcluidoDispMdtoCbSps.I);
		//Melhoria #19063
		Long rQtdeEtiqLidas = getAfaDispMdtoCbSpsDAO().getDispMdtoCbSpsExcluidoByDispensacaoMdtoCount(seqAfaDispMdto, null);
		if(!CoreUtil.igual(qtdeDispensadaNew, qtdeDispensadaOld)
				&& rQtdeEtiqLidas > 0){
			if(rQtdeEtiqLidas == qtdeDispensadaNew.intValue()){
				indSituacao = DominioSituacaoDispensacaoMdto.D;
			}
			
			if(rQtdeEtiqLidas == qtdeDispensadaOld.intValue()
					&& qtdeDispensadaNew.intValue() > rQtdeEtiqLidas
					&& DominioSituacaoDispensacaoMdto.D.equals(indSituacao)){
				indSituacao = DominioSituacaoDispensacaoMdto.T;
			}
			
			if(rQtdeEtiqLidas == qtdeDispensadaOld.intValue()
				&& qtdeDispensadaNew.intValue() <rQtdeEtiqLidas
				&& DominioSituacaoDispensacaoMdto.D.equals(indSituacao)
				|| (rQtdeEtiqLidas > qtdeDispensadaNew.intValue() 
						&& DominioSituacaoDispensacaoMdto.T.equals(indSituacao))
						){
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_01473);
			}
			
		}
		
		return indSituacao;
		
	}

	/**
	 * @ORADB afak_dsm_rn.rn_dsmp_ver_altera
	 * @param indSituacao
	 * @throws ApplicationBusinessException
	 */
	public void rnDsmpVerAltera(DominioSituacaoDispensacaoMdto indSituacao) throws ApplicationBusinessException {
		if(DominioSituacaoDispensacaoMdto.E.equals(indSituacao)) {
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.AFA_00260);
		}
	}
	
	/**
	 * @ORADB PROCEDURE afak_dsm_rn.RN_DSMP_ATU_UNF_SOL
	 * @param dispMdto
	 * @param atdSeq
	 * @return dispMdto
	 * @throws ApplicationBusinessException
	 */
	public AfaDispensacaoMdtos atualizarUnidadeSolicitante(AfaDispensacaoMdtos dispMdto, Integer atdSeq)
			throws ApplicationBusinessException {
		
		AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);
		if (atendimento != null) {
			dispMdto.setUnidadeFuncionalSolicitante(atendimento.getUnidadeFuncional());
		}	
		
		return dispMdto;
	}
	
	/**
	 * @ORADB PROCEDURE AFAK_DSM_RN.RN_DSMP_VER_DELECAO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void lancarExcecaoPermissaoExclusao()
			throws ApplicationBusinessException {
		throw new ApplicationBusinessException(
				AfaDispensacaoMdtosRNExceptionCode.AFA_00258);
	}
	
	protected AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return afaDispensacaoMdtosDAO;
	}
	
	protected AfaDispMdtoCbSpsDAO getAfaDispMdtoCbSpsDAO(){
		return afaDispMdtoCbSpsDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	/**
	 * @throws BaseException 
	 * @ORADB MPMP_GERA_DISP_MVTO
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void mpmpGeraDispMVto(Integer pmeAtdSeq, Integer pmeSeq, Date pmeData, Date pmeDthrInicio, Date pmeDthrFim, String nomeMicrocomputador) throws BaseException{
		
		BigDecimal dose;
		BigDecimal percSeNec;
		DominioSituacaoItemPrescritoDispensacaoMdto sitItem;
		Long pmdSeqAnt = 0l;
		TipoOperacaoEnum operacaoAnt = null;
		boolean indSolucaoAnt = false;
		
		AghParametros percEnvioDisp = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PERC_ENVIO_DISP_MDTO_SN);
		
		if(percEnvioDisp==null || percEnvioDisp.getVlrNumerico()==null){
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.MPM_03088);
		}
		percSeNec = percEnvioDisp.getVlrNumerico();
		
		List<MpmPrescricaoMdto> prescricaoMedicamentos = getPrescricaoMedicaFacade().pesquisarPrescricaoMdtoNovo(pmeAtdSeq, pmeData, pmeDthrInicio, pmeDthrFim);
		
		aplicaTipoOperacaoBancoEOrdena(prescricaoMedicamentos);
		
		for(MpmPrescricaoMdto prescricaoMdto:prescricaoMedicamentos){
			for(MpmItemPrescricaoMdto itemPrescricaoMdto:prescricaoMdto.getItensPrescricaoMdtos()){
				
				Short qtdeCalcSist24h = itemPrescricaoMdto.getQtdeCalcSist24h();
				BigDecimal itemDose = itemPrescricaoMdto.getDose();
				AfaFormaDosagem formaDosagem = itemPrescricaoMdto.getFormaDosagem(); 
				
				dose = mpmcCalcDoseDisp(qtdeCalcSist24h, pmeData, pmeDthrFim, itemDose, formaDosagem.getSeq());
				
				/*if(pmdSeqAnt > 0 && (prescricaoMdto.getPrescricaoMdtoOrigem() != null && pmdSeqAnt != prescricaoMdto.getPrescricaoMdtoOrigem().getId().getSeq())
						&& TipoOperacaoEnum.UPDATE.equals(operacaoAnt)
								&& indSolucaoAnt){
					excluirItensAposAlteracaoDeSolucao(pmeAtdSeq, pmeSeq, pmdSeqAnt, nomeMicrocomputador);
					//Além de comentado na revision 170789 o flush mudou de local
				}*/
				
				if(TipoOperacaoEnum.INSERT.equals(prescricaoMdto.getTipoOperacaoBanco())){
					
					try{
						AfaDispensacaoMdtos dispMdto = processaNovaAfaDispMdto(pmeAtdSeq, pmeSeq,
								itemPrescricaoMdto.getPrescricaoMedicamento().getId().getAtdSeq(), 
								itemPrescricaoMdto.getPrescricaoMedicamento().getId().getSeq(), 
								itemPrescricaoMdto.getMedicamento().getMatCodigo(),
								itemPrescricaoMdto.getId().getSeqp(),
								prescricaoMdto.getIndSeNecessario(),
								dose,
								percSeNec,
								itemPrescricaoMdto.getFormaDosagem().getSeq(),
								DominioSituacaoItemPrescritoDispensacaoMdto.IS, null);
						
						// getAfaDispensacaoMdtosDAO().inserir(dispMdto, false);
						criaDispMdtoTriagemPrescricao(dispMdto, nomeMicrocomputador);
					}catch (BaseRuntimeException e) {
						throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.MPM_01574);
					}
					
				}
				
				if(TipoOperacaoEnum.UPDATE.equals(prescricaoMdto.getTipoOperacaoBanco())){
					this.achouDisp = Boolean.TRUE;
					sitItem = null;
					this.dsmSeq = null;
					
					atualizaPosGeracaoEPosInclusao(
							pmeAtdSeq,
							pmeSeq, itemPrescricaoMdto, prescricaoMdto,
							sitItem, dose, percSeNec, nomeMicrocomputador);
					
						if(this.achouDisp){
							atualizaAlteradoPosGeracaoEAlteradoPosInclusao(pmeAtdSeq, pmeSeq, itemPrescricaoMdto, nomeMicrocomputador);
						}
					}
				
				if(TipoOperacaoEnum.DELETE.equals(prescricaoMdto.getTipoOperacaoBanco())){
					this.achouDisp = Boolean.TRUE;
					sitItem = null;
					this.dsmSeq = null;
					
					List<AfaDispensacaoMdtos> dispensacoes = getAfaDispensacaoMdtosDAO()
						.pesquisarAfaDispensacaoMdto(pmeAtdSeq, pmeSeq,
								itemPrescricaoMdto.getId().getPmdAtdSeq(),
								itemPrescricaoMdto.getId().getPmdSeq(),
								itemPrescricaoMdto.getId().getMedMatCodigo(),
								itemPrescricaoMdto.getId().getSeqp());
					
					if(dispensacoes == null || dispensacoes.isEmpty()){
						this.achouDisp = Boolean.FALSE;
					}else{
						this.dsmSeq = dispensacoes.get(0).getSeq();
						sitItem = dispensacoes.get(0).getIndSitItemPrescrito();
					}
					
					if(this.achouDisp){
						atualizaExcluidoPosGeracaoEPosInclusao(pmeAtdSeq, pmeSeq, itemPrescricaoMdto, sitItem, nomeMicrocomputador);
					}
				}
				
				pmdSeqAnt = prescricaoMdto.getPrescricaoMdtoOrigem() != null ? prescricaoMdto.getPrescricaoMdtoOrigem().getId().getSeq() : 0L;
				operacaoAnt = prescricaoMdto.getTipoOperacaoBanco();
				indSolucaoAnt = prescricaoMdto.getIndSolucao();
			}
		}
		if(TipoOperacaoEnum.UPDATE.equals(operacaoAnt) && indSolucaoAnt && pmdSeqAnt !=null && pmdSeqAnt != 0L){
			excluirItensAposAlteracaoDeSolucao(pmeAtdSeq, pmeSeq, pmdSeqAnt, nomeMicrocomputador);
		}
	}

	private void atualizaExcluidoPosGeracaoEPosInclusao(Integer pmeAtdSeq,
			Integer pmeSeq, MpmItemPrescricaoMdto itemPrescricaoMdto,
			DominioSituacaoItemPrescritoDispensacaoMdto sitItem, String nomeMicrocomputador)
			throws BaseException {
		List<AfaDispensacaoMdtos> dispensacoes = getAfaDispensacaoMdtosDAO()
			.pesquisarAfaDispensacaoMdto(pmeAtdSeq, pmeSeq,
					itemPrescricaoMdto.getId().getPmdAtdSeq(),
					itemPrescricaoMdto.getId().getPmdSeq(),
					itemPrescricaoMdto.getId().getMedMatCodigo(),
					itemPrescricaoMdto.getId().getSeqp(),
					this.dsmSeq);
		
		for (AfaDispensacaoMdtos adm : dispensacoes) {
			AfaDispensacaoMdtos admOld = getAfaDispOldDesatachado(adm);
			
			sitItem = adm.getIndSitItemPrescrito();
			this.dsmSeq = adm.getSeq();
			
			if(DominioSituacaoItemPrescritoDispensacaoMdto.GP.equals(sitItem)){
				sitItem = DominioSituacaoItemPrescritoDispensacaoMdto.EG;
			}else if(DominioSituacaoItemPrescritoDispensacaoMdto.PG.equals(sitItem)){
				sitItem = DominioSituacaoItemPrescritoDispensacaoMdto.EG;
			}else if(DominioSituacaoItemPrescritoDispensacaoMdto.IS.equals(sitItem)){
				sitItem = DominioSituacaoItemPrescritoDispensacaoMdto.EI;
			}else if(DominioSituacaoItemPrescritoDispensacaoMdto.PI.equals(sitItem)){
				sitItem = DominioSituacaoItemPrescritoDispensacaoMdto.EI;
			}
			adm.setIndSitItemPrescrito(sitItem);
			
			try {
				//getAfaDispensacaoMdtosDAO().atualizarDepreciado(adm);
				atualizaAfaDispMdto(adm, admOld, nomeMicrocomputador);
			} catch (BaseRuntimeException e) {
				throw new ApplicationBusinessException(
						AfaDispensacaoMdtosRNExceptionCode.MPM_03090);
			}
			
		}
	}

	protected void atualizaAlteradoPosGeracaoEAlteradoPosInclusao(
			Integer pmeAtdSeq, Integer pmeSeq,
			MpmItemPrescricaoMdto itemPrescricaoMdto, String nomeMicrocomputador)
			throws BaseException {
		List<AfaDispensacaoMdtos> dispensacoes = getAfaDispensacaoMdtosDAO()
				.pesquisarAfaDispensacaoMdto(
						itemPrescricaoMdto.getPrescricaoMedicamento().getPrescricaoMdtoOrigem().getId().getAtdSeq(),
						pmeSeq,
						//pmeAtdSeq,	pmeSeq,
						
						/*itemPrescricaoMdto.getPrescricaoMedicamento().getId().getAtdSeq(),
						itemPrescricaoMdto.getPrescricaoMedicamento().getId().getSeq(),
						itemPrescricaoMdto.getMedicamento().getMatCodigo(),
						itemPrescricaoMdto.getId().getSeqp(), 
						*/
						itemPrescricaoMdto.getPrescricaoMedicamento().getPrescricaoMdtoOrigem().getId().getAtdSeq(),
						itemPrescricaoMdto.getPrescricaoMedicamento().getPrescricaoMdtoOrigem().getId().getSeq(),
						itemPrescricaoMdto.getId().getMedMatCodigo(),
						itemPrescricaoMdto.getId().getSeqp(),
						this.dsmSeq);

		for (AfaDispensacaoMdtos adm : dispensacoes) {
			AfaDispensacaoMdtos admOld = getAfaDispOldDesatachado(adm);
			
			DominioSituacaoItemPrescritoDispensacaoMdto novaSituacaoItem = adm
					.getIndSitItemPrescrito();
			if (DominioSituacaoItemPrescritoDispensacaoMdto.GP.equals(
					adm.getIndSitItemPrescrito())){
				novaSituacaoItem = DominioSituacaoItemPrescritoDispensacaoMdto.DG;
			}else if (DominioSituacaoItemPrescritoDispensacaoMdto.IS.equals(
					adm.getIndSitItemPrescrito())){
				novaSituacaoItem = DominioSituacaoItemPrescritoDispensacaoMdto.DI;
			}
			adm.setIndSitItemPrescrito(novaSituacaoItem);

			try {
				//getAfaDispensacaoMdtosDAO().atualizarDepreciado(adm);
				atualizaAfaDispMdto(adm, admOld, nomeMicrocomputador);
			} catch (BaseRuntimeException e) {
				throw new ApplicationBusinessException(
						AfaDispensacaoMdtosRNExceptionCode.MPM_03090);
			}
		}

	}

	public AfaDispensacaoMdtos getAfaDispOldDesatachado(AfaDispensacaoMdtos adm) throws ApplicationBusinessException {
		
		adm.getMedicamento();
		adm.getServidor();
		adm.getServidorConferida();
		adm.getServidorEntregue();
		adm.getServidorEstornado();
		adm.getServidorTriadoPor();
		AfaDispensacaoMdtos admOld;
		try {
			admOld = (AfaDispensacaoMdtos) BeanUtils.cloneBean(adm);
		} catch (IllegalAccessException e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.PROBLEMA_EXECUTAR_OPERACAO);
		} catch (InstantiationException e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.PROBLEMA_EXECUTAR_OPERACAO);
		} catch (InvocationTargetException e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.PROBLEMA_EXECUTAR_OPERACAO);
		} catch (NoSuchMethodException e) {
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.PROBLEMA_EXECUTAR_OPERACAO);
		}
		
		return admOld;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private void atualizaPosGeracaoEPosInclusao(Integer pmeAtdSeq,
			Integer pmeSeq, MpmItemPrescricaoMdto itemPrescricaoMdto,
			MpmPrescricaoMdto prescricaoMdto,
			DominioSituacaoItemPrescritoDispensacaoMdto sitItem,
			BigDecimal dose, BigDecimal percSeNec, String nomeMicrocomputador) throws ApplicationBusinessException,
			ApplicationBusinessException {
		
		List<AfaDispensacaoMdtos> dispensacoes = getAfaDispensacaoMdtosDAO()
				.pesquisarAfaDispensacaoMdto(
						prescricaoMdto.getPrescricaoMdtoOrigem() !=null ? prescricaoMdto.getPrescricaoMdtoOrigem().getId().getAtdSeq() : null, 
						pmeSeq,
						prescricaoMdto.getPrescricaoMdtoOrigem() !=null ? prescricaoMdto.getPrescricaoMdtoOrigem().getId().getAtdSeq() : null,
						prescricaoMdto.getPrescricaoMdtoOrigem() !=null ? prescricaoMdto.getPrescricaoMdtoOrigem().getId().getSeq() : null,
						itemPrescricaoMdto.getId().getMedMatCodigo(),
						itemPrescricaoMdto.getId().getSeqp());

		if (dispensacoes == null || dispensacoes.isEmpty()) {
			this.achouDisp = Boolean.FALSE;
			this.dsmSeq = null;
		}else{
			sitItem = dispensacoes.get(0).getIndSitItemPrescrito();
			this.dsmSeq = dispensacoes.get(0).getSeq();
		}

		//for(AfaDispensacaoMdtos afaDispMdto:dispensacoes){
		// AfaDispensacaoMdtos afaDispMdto = new AfaDispensacaoMdtos();
			/*sitItem = afaDispMdto.getIndSitItemPrescrito();
			dsmSeq = afaDispMdto.getSeq();*/

			if (DominioSituacaoItemPrescritoDispensacaoMdto.GP.equals(sitItem)){
				sitItem = DominioSituacaoItemPrescritoDispensacaoMdto.PG;
			}else if (DominioSituacaoItemPrescritoDispensacaoMdto.IS.equals(sitItem)){
				sitItem = DominioSituacaoItemPrescritoDispensacaoMdto.PI;
			}else if (DominioSituacaoItemPrescritoDispensacaoMdto.PG.equals(sitItem)){
				sitItem = DominioSituacaoItemPrescritoDispensacaoMdto.PG;
			}else if (DominioSituacaoItemPrescritoDispensacaoMdto.PI.equals(sitItem)){
				sitItem = DominioSituacaoItemPrescritoDispensacaoMdto.PI;
			}else {
				sitItem = DominioSituacaoItemPrescritoDispensacaoMdto.IS;
			}
			try{
				AfaDispensacaoMdtos dispMdto = processaNovaAfaDispMdto(pmeAtdSeq, pmeSeq, itemPrescricaoMdto
						.getPrescricaoMedicamento().getId().getAtdSeq(),
						itemPrescricaoMdto.getPrescricaoMedicamento().getId().getSeq(),
						itemPrescricaoMdto.getMedicamento().getMatCodigo(),
						itemPrescricaoMdto.getId().getSeqp(),
						prescricaoMdto.getIndSeNecessario(), dose,
						percSeNec, itemPrescricaoMdto.getFormaDosagem().getSeq(), 
						sitItem, this.dsmSeq);
				
				//getAfaDispensacaoMdtosDAO().inserir(dispMdto, false);
				criaDispMdtoTriagemPrescricao(dispMdto, nomeMicrocomputador);
			}catch (BaseRuntimeException e) {
				throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.MPM_01574);
			}
		//}

	}

	public AfaDispensacaoMdtos processaNovaAfaDispMdto(Integer atdSeq, Integer pmeSeq,
			Integer pmdAtdSeq, Long pmdSeq, Integer medMatCodigo, Short imeSeqp, 
			Boolean prescricaoMedicamentoIndSeNecessario, BigDecimal dose,
			BigDecimal percSeNecessario, Integer fdsSeq,
			DominioSituacaoItemPrescritoDispensacaoMdto indSitItemPrescrito, Long dsmSequencia,
			Boolean indPmNaoEletronica, Long prmSeq) throws ApplicationBusinessException {
	
		AfaDispensacaoMdtos newAfa = processaNovaAfaDispMdto(atdSeq, pmeSeq,
				pmdAtdSeq, pmdSeq, medMatCodigo, imeSeqp,
				prescricaoMedicamentoIndSeNecessario, dose, percSeNecessario,
				fdsSeq, indSitItemPrescrito, dsmSequencia);
		
		newAfa.setIndPmNaoEletronica(indPmNaoEletronica);
		newAfa.setPrescricaoMedicamento(afaPrescricaoMedicamentoDAO.obterPorChavePrimaria(prmSeq));		
		return newAfa;
	}
	
	public AfaDispensacaoMdtos processaNovaAfaDispMdto(Integer pmeAtdSeq, Integer pmeSeq,
			Integer pmdAtdSeq, Long pmdSeq, Integer medMatCodigo, Short imeSeqp, 
			Boolean prescricaoMedicamentoIndSeNecessario, BigDecimal dose,
			BigDecimal percSeNecessario, Integer fdsSeq,
			DominioSituacaoItemPrescritoDispensacaoMdto indSitItemPrescrito, Long dsmSequencia) throws ApplicationBusinessException {
		
		AfaDispensacaoMdtos adm = new AfaDispensacaoMdtos();
		
		adm.setPrescricaoMedica(processaMpmPrescricaoMedica(pmeAtdSeq, pmeSeq));
		adm.setItemPrescricaoMdto(processaItemPrescricaoMdto(pmdAtdSeq, pmdSeq, medMatCodigo, imeSeqp));
		adm.setSeq(null);
		adm.setMedicamento(processaAfaMedicamento(medMatCodigo));
		adm.setServidor(null);
		adm.setServidorEntregue(null);
		adm.setCriadoEm(null);
		if(prescricaoMedicamentoIndSeNecessario){
			adm.setQtdeDispensada(calculaQtdeByDose(dose, percSeNecessario));
		}
		else{
			adm.setQtdeDispensada(dose);
		}
		adm.setIndSituacao(DominioSituacaoDispensacaoMdto.S);
		adm.setIndSitItemPrescrito(indSitItemPrescrito);
		adm.setTipoOcorrenciaDispensacao(null);
		adm.setUnidadeFuncional(processaUnidadeFuncional(pmeAtdSeq, medMatCodigo, dose, fdsSeq));
		adm.setDthrEntrega(null);
		if(prescricaoMedicamentoIndSeNecessario){
			adm.setQtdeSolicitada(calculaQtdeByDose(dose, percSeNecessario));
		}else{
			adm.setQtdeSolicitada(dose);
		}
		adm.setAtendimento(processaAghAtendimento(pmeAtdSeq));
		adm.setDsmSeq(dsmSequencia);
		
		return adm;
	}

	private AghAtendimentos processaAghAtendimento(Integer pmeAtdSeq) {
		return getAghuFacade().obterAghAtendimentoPorChavePrimaria(pmeAtdSeq);
	}

	public AghUnidadesFuncionais processaUnidadeFuncional(
			Integer atendimentoSeq, Integer medMatCodigo, BigDecimal dose,
			Integer fdsSeq) throws ApplicationBusinessException {
		Short seqUnf = getFarmaciaDispensacaoFacade().getLocalDispensa2(
				atendimentoSeq, medMatCodigo, dose, fdsSeq, null);
		return getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(seqUnf);
	}

	private BigDecimal calculaQtdeByDose(BigDecimal dose,
			BigDecimal percSeNecessario) {
		return dose.multiply(percSeNecessario).divide(new BigDecimal(100), 0, RoundingMode.UP);
	}

	private AfaMedicamento processaAfaMedicamento(Integer medMatCodigo) {
		return getFarmaciaFacade().obterMedicamento(medMatCodigo);
	}

	private MpmItemPrescricaoMdto processaItemPrescricaoMdto(Integer pmdAtdSeq,
			Long pmdSeq, Integer medMatCodigo, Short imeSeqp) {
		MpmItemPrescricaoMdtoId id = new MpmItemPrescricaoMdtoId();
		id.setMedMatCodigo(medMatCodigo);
		id.setPmdAtdSeq(pmdAtdSeq);
		id.setPmdSeq(pmdSeq);
		id.setSeqp(imeSeqp);
		return getPrescricaoMedicaFacade().obterMpmItemPrescricaoMdtoPorChavePrimaria(id);
	}

	private MpmPrescricaoMedica processaMpmPrescricaoMedica(Integer pmeAtdSeq,
			Integer pmeSeq) {
		MpmPrescricaoMedicaId idPrescricao = new MpmPrescricaoMedicaId();
		idPrescricao.setAtdSeq(pmeAtdSeq);
		idPrescricao.setSeq(pmeSeq);

		return getPrescricaoMedicaFacade().obterMpmPrescricaoMedicaPorChavePrimaria(idPrescricao);
	}

	private IFarmaciaFacade getFarmaciaFacade(){
		return this.farmaciaFacade;
	}
	
	/**
	 * EXCLUSÃO DE ITENS APÓS ALTERAÇÃO DE SOLUÇÃO
	 * @param pmdSeqAnt 
	 * @param pmeSeq 
	 * @param pmeAtdSeq 
	 * @throws BaseException 
	 */
	protected void excluirItensAposAlteracaoDeSolucao(Integer pmeAtdSeq,
			Integer pmeSeq, Long pmdSeqAnt, String nomeMicrocomputador) throws BaseException {
		List<AfaDispensacaoMdtos> dispensacoes = getAfaDispensacaoMdtosDAO()
				.pesquisarAfaDispensacaoMdto(pmeAtdSeq, pmeSeq, pmdSeqAnt,
						DominioSituacaoItemPrescritoDispensacaoMdto.DG,
						DominioSituacaoItemPrescritoDispensacaoMdto.DI);

		if(dispensacoes!=null && !dispensacoes.isEmpty()){
			for(AfaDispensacaoMdtos adm:dispensacoes){
				AfaDispensacaoMdtos admOld = getAfaDispOldDesatachado(adm);
				
				DominioSituacaoItemPrescritoDispensacaoMdto novaSituacaoItemPrescrito = adm.getIndSitItemPrescrito();
				
				if(DominioSituacaoItemPrescritoDispensacaoMdto.GP.equals(adm.getIndSitItemPrescrito())){
					novaSituacaoItemPrescrito = DominioSituacaoItemPrescritoDispensacaoMdto.EG;
				}else if(DominioSituacaoItemPrescritoDispensacaoMdto.PG.equals(adm.getIndSitItemPrescrito())){
					novaSituacaoItemPrescrito = DominioSituacaoItemPrescritoDispensacaoMdto.EG;
				}else if(DominioSituacaoItemPrescritoDispensacaoMdto.IS.equals(adm.getIndSitItemPrescrito())){
					novaSituacaoItemPrescrito = DominioSituacaoItemPrescritoDispensacaoMdto.EI;
				}else if(DominioSituacaoItemPrescritoDispensacaoMdto.PI.equals(adm.getIndSitItemPrescrito())){
					novaSituacaoItemPrescrito = DominioSituacaoItemPrescritoDispensacaoMdto.EI;
				}
				adm.setIndSitItemPrescrito(novaSituacaoItemPrescrito);
				
				try{
					//getAfaDispensacaoMdtosDAO().atualizarDepreciado(adm);
					atualizaAfaDispMdto(adm, admOld, nomeMicrocomputador);
					}catch (BaseRuntimeException e) {
						throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.MPM_03097);
					}
				}
			}
	}

	/**
	 * Aplica teste nos itens de MpmPrescricaoMdto para verificar operação que está ocorrendo
	 * @param prescricaoMedicamentos
	 */
	private void aplicaTipoOperacaoBancoEOrdena(
			List<MpmPrescricaoMdto> prescricaoMedicamentos) {
		
		for(MpmPrescricaoMdto prescricaoMdto:prescricaoMedicamentos){
			prescricaoMdto.setTipoOperacaoBanco(mpmcGetOperMvto(prescricaoMdto
					.getAlteradoEm(), 
					prescricaoMdto.getPrescricaoMdtoOrigem() != null ? prescricaoMdto.getPrescricaoMdtoOrigem().getId().getAtdSeq() : null,
					prescricaoMdto.getPrescricaoMdtoOrigem() != null ? prescricaoMdto.getPrescricaoMdtoOrigem().getId().getSeq() : null));
			
		}
		
		//ORDER BY mpmc_get_oper_mvto(PMD.ALTERADO_EM, PMD.PMD_ATD_SEQ, PMD.PMD_SEQ), pmd.atd_seq, pmd.seq;
		CoreUtil.ordenarLista(prescricaoMedicamentos, MpmPrescricaoMdto.Fields.SEQ.toString(), true);
		CoreUtil.ordenarLista(prescricaoMedicamentos, MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), true);
		CoreUtil.ordenarLista(prescricaoMedicamentos, "tipoOperacaoBanco", true);
		
	}

	/**
	 * @ORADB MPMC_CALC_DOSE_DISP
	 * @param qtdeCalcSist24h
	 * @param pmeData
	 * @param pmeDthrFim
	 * @param dose
	 * @param seq
	 * @return
	 */
	public BigDecimal mpmcCalcDoseDisp(Short dose24h, Date dthrInicio,
			Date pmeDthrFim, BigDecimal dose, Integer fdsSeq) {
		if(dose24h==null || dose24h == 0){
			return new BigDecimal(dose24h);
		}
		
		BigDecimal bigQtdeCalcSis24h = new BigDecimal(dose24h);
		
		AfaFormaDosagem formaDosagem = getFarmaciaFacade().obterAfaFormaDosagem(fdsSeq);
		//Alterei para realizar a divisão sem limitar o número de casas após a vírgula pois não faz diferença
		//uma vez que logo em seguida o valor é arredondado para cima
		float divisao = dose.floatValue()/formaDosagem.getFatorConversaoUp().floatValue();
		BigDecimal doseUnitaria = new BigDecimal(divisao);
		doseUnitaria = doseUnitaria.setScale(0, RoundingMode.UP);
		
		BigDecimal qtdeVezes = bigQtdeCalcSis24h.divide(doseUnitaria, 4, RoundingMode.FLOOR);
		BigDecimal qtdeDias = DateUtil.calcularDiasEntreDatasComPrecisao(dthrInicio,pmeDthrFim);
		
		if(qtdeDias.compareTo(BigDecimal.ONE) == BigDecimal.ZERO.intValue()){
			return bigQtdeCalcSis24h;
		}else{
			BigDecimal doseDisp = qtdeVezes.multiply(qtdeDias).setScale(0, RoundingMode.UP).multiply(doseUnitaria);//doseUnitaria;//
			if(doseDisp.compareTo(bigQtdeCalcSis24h)==1){
				return bigQtdeCalcSis24h;
			}else{
				return doseDisp;
			}
		}
	}
	
	/**
	 * @ORADB MPMC_GET_OPER_MVTO
	 * Esta function determina a operação que o usuário fez com os dados da prescrição médica
	 */
	public TipoOperacaoEnum mpmcGetOperMvto(Date pmdAlteradoEm, Integer pmdAtdSeq, Long pmdSeq){
		if(pmdAlteradoEm==null){
			if(pmdAtdSeq==null && pmdSeq==null){
				return TipoOperacaoEnum.INSERT;//1
			}else{
				return TipoOperacaoEnum.UPDATE;//2
			}
		}else{
			return TipoOperacaoEnum.DELETE;//3
		}
	}
	
	
	/**
	 * @ORADB MPMP_GERA_DISP_TOT
	 * esta rotina irá gerar a dispensação de medicamentos levando em conta os
   	 * medicamentos vigentes para uma determinada prescrição médica
	 */
	public void mpmpGeraDispTot(Integer pmeAtdSeq, Integer pmeSeq, Date pmeDthrInicio, Date pmeDthrFim, String nomeMicrocomputador) throws ApplicationBusinessException{
		BigDecimal percSeNec;
		BigDecimal dose;
		
		AghParametros percEnvioDisp = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PERC_ENVIO_DISP_MDTO_SN);
		
		if(percEnvioDisp==null || percEnvioDisp.getVlrNumerico()==null) {
			throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.MPM_01708);
		}
		
		percSeNec = percEnvioDisp.getVlrNumerico();
		
		List<MpmPrescricaoMdto> prescricaoMedicamentos = getPrescricaoMedicaFacade().pesquisarPrescricaoMdtoSitConfirmado(pmeAtdSeq, pmeSeq);
		
		for(MpmPrescricaoMdto prescricaoMdto:prescricaoMedicamentos){
			prescricaoMdto.setTipoOperacaoBanco(TipoOperacaoEnum.INSERT);
			farmaciaFacade.processaDispensacaoDiluente(
					prescricaoMdto, percSeNec, pmeDthrInicio, pmeDthrFim,
					nomeMicrocomputador, Boolean.TRUE);
			for(MpmItemPrescricaoMdto itemPrescricaoMdto:prescricaoMdto.getItensPrescricaoMdtos()){
				
				Short qtdeCalcSist24h = itemPrescricaoMdto.getQtdeCalcSist24h();
				BigDecimal itemDose = itemPrescricaoMdto.getDose();
				AfaFormaDosagem formaDosagem = itemPrescricaoMdto.getFormaDosagem(); 
				
				dose = mpmcCalcDoseDisp(qtdeCalcSist24h, pmeDthrInicio, pmeDthrFim, itemDose, formaDosagem.getSeq());
				try{
					AfaDispensacaoMdtos dispMdto = processaNovaAfaDispMdto(pmeAtdSeq, pmeSeq,
							itemPrescricaoMdto.getPrescricaoMedicamento().getId().getAtdSeq(), 
							itemPrescricaoMdto.getPrescricaoMedicamento().getId().getSeq(), 
							itemPrescricaoMdto.getMedicamento().getMatCodigo(),
							itemPrescricaoMdto.getId().getSeqp(),
							prescricaoMdto.getIndSeNecessario(),
							dose,
							percSeNec,
							itemPrescricaoMdto.getFormaDosagem().getSeq(),
							DominioSituacaoItemPrescritoDispensacaoMdto.GP, null);
					
					//getAfaDispensacaoMdtosDAO().inserir(dispMdto, false);
					criaDispMdtoTriagemPrescricao(dispMdto, nomeMicrocomputador);
				}catch (BaseRuntimeException e) {
					throw new ApplicationBusinessException(AfaDispensacaoMdtosRNExceptionCode.MPM_01573);
				}
			}
		}
		
	}
	
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	protected IFarmaciaDispensacaoFacade getFarmaciaDispensacaoFacade() {
		return this.farmaciaDispensacaoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
