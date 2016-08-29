package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.MpmAltaPedidoExame;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPedidoExameDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ManterAltaPedidoExameRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaPedidoExameRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaSumarioDAO mpmAltaSumarioDAO;

@Inject
private MpmAltaPedidoExameDAO mpmAltaPedidoExameDAO;

@EJB
private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1931918960675454013L;

	public enum ManterAltaPedidoExameRNExceptionCode implements
			BusinessExceptionCode {

		ERRO_INSERIR_ALTA_PEDIDO_EXAME, ERRO_EXCLUIR_ALTA_PEDIDO_EXAME, ERRO_ALTERAR_ALTA_PEDIDO_EXAME, MPM_02678, MPM_02679, MPM_02674, MPM_02675, MPM_02953, MPM_03357, MPM_03712, MPM_02957, MPM_02958, MPM_02959, MPM_02960, MPM_03358, MPM_02964, MPM_03735, MPM_02661, MPM_02890;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

		public void throwException(Throwable cause, Object... params)
				throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}

	/**
	 * Insere objeto MpmAltaPedidoExame.
	 * 
	 * @param {MpmAltaPedidoExame} altaPedidoExame
	 */
	public void inserirAltaPedidoExame(MpmAltaPedidoExame altaPedidoExame)
			throws ApplicationBusinessException {

		try {

			this.preInserirAltaPedidoExame(altaPedidoExame);
			this.getAltaPedidoExameDAO().persistir(altaPedidoExame);
			this.getAltaPedidoExameDAO().flush();

		} catch (Exception e) {

			logError(e.getMessage(), e);
			ManterAltaPedidoExameRNExceptionCode.ERRO_INSERIR_ALTA_PEDIDO_EXAME
					.throwException(e);

		}

	}

	
	/**
	 * Insere/Atualiza objeto MpmAltaPedidoExame.
	 * @param {MpmAltaPedidoExame} altaPedidoExame
	 * @return Mensagem de confirmacao
	 */
	public String gravarAltaPedidoExame(MpmAltaPedidoExame altaPedidoExame)
			throws ApplicationBusinessException {
		
		String confirmacao = null;
		
		if (altaPedidoExame.getId() == null) {// Inserir Alta Pedido Exame
			
			inserirAltaPedidoExame(altaPedidoExame);
			confirmacao = "MENSAGEM_SUCESSO_INCLUSAO_ALTA_PREVISAO_CONSULTA_AMBULATORIAL";
			
		} else { // Atualizar Alta Pedido Exame
	
			atualizarAltaPedidoExame(altaPedidoExame);
			confirmacao = "MENSAGEM_SUCESSO_ALTERACAO_ALTA_PREVISAO_CONSULTA_AMBULATORIAL";
					
		}
		
		return confirmacao;
	}	
	
	
	/**
	 * Insere objeto MpmAltaPedidoExame.
	 * 
	 * @param {MpmAltaPedidoExame} altaPedidoExame
	 */
	public void atualizarAltaPedidoExame(MpmAltaPedidoExame altaPedidoExame)
			throws ApplicationBusinessException {
		try {
			this.preAtualizarAltaPedidoExame(altaPedidoExame);
			this.getAltaPedidoExameDAO().atualizar(altaPedidoExame);
			this.getAltaPedidoExameDAO().flush();
		} catch (Exception e) {
			logError(e.getMessage(), e);
			ManterAltaPedidoExameRNExceptionCode.ERRO_ALTERAR_ALTA_PEDIDO_EXAME
					.throwException(e);
		}
	}
	
	/**
	 * Remove objeto MpmAltaPedidoExame.
	 * 
	 * @param {MpmAltaPedidoExame} altaPedidoExame
	 */
	public void excluirAltaPedidoExame(MpmAltaPedidoExame altaPedidoExame) throws ApplicationBusinessException {

		try {
			
			// Verifica se ALTA_SUMARIOS está ativo
			getAltaSumarioRN().verificarAltaSumarioAtivo(altaPedidoExame.getAltaSumario());
			this.getAltaPedidoExameDAO().remover(altaPedidoExame);
			this.getAltaPedidoExameDAO().flush();

		} catch (Exception e) {

			logError(e.getMessage(), e);
			ManterAltaPedidoExameRNExceptionCode.ERRO_EXCLUIR_ALTA_PEDIDO_EXAME
					.throwException(e);

		}

	}

	/**
	 * ORADB Trigger MPMT_AEX_BRI
	 * 
	 * @param {MpmAltaPedidoExame} altaPedidoExame
	 */
	protected void preInserirAltaPedidoExame(MpmAltaPedidoExame altaPedidoExame)
			throws ApplicationBusinessException {

		// Verifica se ALTA_SUMARIOS está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(altaPedidoExame.getAltaSumario());
		
		Short preEspSeq = null;
		Short espSeq = null;
		Short unfSeq = null;
		Integer eqpSeq = null;
		Integer matriculaServidor = null;
		Short vinculoServidor = null;
		Byte uslSala = null;
		Short uslUnfSeq = null;
		
		if (altaPedidoExame.getAghProfEspecialidades() != null) {
			preEspSeq = altaPedidoExame.getAghProfEspecialidades().getAghEspecialidade().getSeq();
			matriculaServidor = altaPedidoExame.getAghProfEspecialidades().getRapServidor().getId().getMatricula();
			vinculoServidor = altaPedidoExame.getAghProfEspecialidades().getRapServidor().getId().getVinCodigo();
		}
		
		if (altaPedidoExame.getAghEquipes() != null) {
			
			eqpSeq = altaPedidoExame.getAghEquipes().getSeq();
			
		}
		
		if (altaPedidoExame.getAghEspecialidades() != null) {
			
			espSeq = altaPedidoExame.getAghEspecialidades().getSeq();
			
		}
		
		if (altaPedidoExame.getAacUnidFuncionalSalas() != null) {
			
			uslSala = altaPedidoExame.getAacUnidFuncionalSalas().getId().getSala();
			uslUnfSeq = altaPedidoExame.getAacUnidFuncionalSalas().getUnidadeFuncional().getSeq();
			
		}
		
		if (altaPedidoExame.getAghUnidadesFuncionais() != null) {
			
			unfSeq = altaPedidoExame.getAghUnidadesFuncionais().getSeq();
			
		}

		// Verifica se atendimento é sus
		this.verificarAtendimento(altaPedidoExame.getAltaSumario().getId(), uslSala,
				uslUnfSeq, preEspSeq, matriculaServidor,
						vinculoServidor, eqpSeq, espSeq, altaPedidoExame
						.getIndAgenda(), altaPedidoExame.getDthrConsulta());

		// Verifica dados do pedido de agendamento de consulta
		this.verificarConsulta(espSeq,
				eqpSeq, preEspSeq, matriculaServidor,
				vinculoServidor, uslSala,
				uslUnfSeq, altaPedidoExame
						.getIndAgenda(), altaPedidoExame.getDthrConsulta(),
				altaPedidoExame.getFatConvenioSaudePlano().getId().getSeq(),
				altaPedidoExame.getFatConvenioSaudePlano().getId()
						.getCnvCodigo(), unfSeq);

		// Verifica se chave e descrição estão preenchidos para equipe
		this.verificarEquipe(eqpSeq, altaPedidoExame.getDescEquipe());

		// Verifica se chave e descrição estão preenchidos para unidade
		this.verificarUnidade(altaPedidoExame.getDescUnidade(), uslSala, uslUnfSeq,
				unfSeq);

		if (altaPedidoExame.getDthrConsulta() != null) {

			// Verifica se a dthr_consulta é maior que sysdate
			this.verificarDtHrConsulta(altaPedidoExame.getDthrConsulta());

		}

	}

	/**
	 * ORADB Trigger MPMT_AEX_BRI
	 * 
	 * @param {MpmAltaPedidoExame} altaPedidoExame
	 */
	protected void preAtualizarAltaPedidoExame(MpmAltaPedidoExame altaPedidoExame) throws ApplicationBusinessException{
		preInserirAltaPedidoExame(altaPedidoExame);
	}
	
	
	/**
	 * ORADB Procedure mpmk_aex_rn.rn_aexp_ver_dthr_con
	 * 
	 * Descrição: A dthr_consulta deve ser maior que sysdate.
	 * 
	 * @param {Date} novoDthrConsulta
	 *  
	 */
	public void verificarDtHrConsulta(Date novoDthrConsulta) throws ApplicationBusinessException {

		if (DateUtil.validaDataTruncadaMaior(new Date(), novoDthrConsulta)) {

			ManterAltaPedidoExameRNExceptionCode.MPM_02890.throwException();

		}

	}

	/**
	 * ORADB Procedure mpmk_aex_rn.rn_aexp_ver_atend
	 * 
	 * Operação: INS, UPD
	 * 
	 * Descrição: Só podem estar preenchidas as fk's UNID FUNCIONAL SALA,
	 * PROFISSIONAL ESPECIALIDADE, EQUIPE, ESPECIALIDADE, e os atributos
	 * ind_agenda e dthr_consulta se o atendimento (consulta) for do convênio
	 * SUS.
	 * 
	 * @param {Integer} novoAsuApaAtdSeq
	 * @param {Integer} novoAsuApaSeq
	 * @param {Short} novoAsuSeqp
	 * @param {Byte} novoUslSala
	 * @param {Short} novoUslUnfSeq
	 * @param {Short} novoPreEspSeq
	 * @param {Integer} novoPreSerMatricula
	 * @param {Short} novoPreSerVinCodigo
	 * @param {Integer} novoEqpSeq
	 * @param {Short} novoEspSeq
	 * @param {Boolean} novoIndAgenda
	 * @param {Date} novoDthrConsulta
	 */
	public void verificarAtendimento(MpmAltaSumarioId altaSumarioId, Byte novoUslSala,
			Short novoUslUnfSeq, Short novoPreEspSeq,
			Integer novoPreSerMatricula, Short novoPreSerVinCodigo,
			Integer novoEqpSeq, Short novoEspSeq, Boolean novoIndAgenda,
			Date novoDthrConsulta) throws ApplicationBusinessException {

		MpmAltaSumario altaSumario = getAltaSumarioDAO()
				.obterAltaSumarioPeloIdESituacao(altaSumarioId,
						DominioSituacao.A);

		if (altaSumario != null) {

			if (!Short.valueOf("1").equals(
					altaSumario.getConvenioSaudePlano().getConvenioSaude()
							.getCodigo())) {

				if (novoUslSala != null || novoUslUnfSeq != null
						|| novoPreEspSeq != null || novoPreSerMatricula != null
						|| novoPreSerVinCodigo != null || novoEqpSeq != null
						|| novoEspSeq != null || novoIndAgenda != null
						|| novoDthrConsulta != null) {

					// Informação só é válida para atendimento SUS
					ManterAltaPedidoExameRNExceptionCode.MPM_02661
							.throwException();

				}

			}

		} else {

			// Sumário de alta está inativo.
			ManterAltaPedidoExameRNExceptionCode.MPM_03735.throwException();

		}

	}

	/**
	 * ORADB Procedure mpmk_aex_rn.rn_aexp_ver_consulta
	 * 
	 * Operação: INS, UPD
	 * 
	 * Descrição: O preenchimento dos campos do pedido de solicitação de exames
	 * deve ser feito conforme o grupo do convenio informado no pedido.
	 * 
	 * @param {Short} novoEspSeq
	 * @param {Integer} novoEqpSeq
	 * @param {Short} novoPreEspSeq
	 * @param {Integer} novoPreSerMatricula
	 * @param {Short} novoPreSerVinCodigo
	 * @param {Byte} novoUslSala
	 * @param {Short} novoUslUnfSeq
	 * @param {Boolean} novoIndAgenda
	 * @param {Date} novoDthrConsulta
	 * @param {Byte} novoCspSeq
	 * @param {Short} novoCspCnvCodigo
	 * @param {Short} novoUnfSeq
	 */
	@SuppressWarnings({"PMD.NPathComplexity"})
	public void verificarConsulta(Short novoEspSeq, Integer novoEqpSeq,
			Short novoPreEspSeq, Integer novoPreSerMatricula,
			Short novoPreSerVinCodigo, Byte novoUslSala, Short novoUslUnfSeq,
			Boolean novoIndAgenda, Date novoDthrConsulta, Byte novoCspSeq,
			Short novoCspCnvCodigo, Short novoUnfSeq)
			throws ApplicationBusinessException {

		FatConvenioSaude convenioSaude = getFaturamentoApoioFacade()
				.obterConvenioSaude(novoCspCnvCodigo);

		if (convenioSaude != null) {

			if (DominioGrupoConvenio.S.equals(convenioSaude.getGrupoConvenio())) {

				if (novoEspSeq == null) {

					ManterAltaPedidoExameRNExceptionCode.MPM_02953
							.throwException();

				}

				if (novoUslSala == null && novoUslUnfSeq == null
						&& novoUnfSeq == null) {

					ManterAltaPedidoExameRNExceptionCode.MPM_03357
							.throwException();

				}

				if (novoUslSala != null && novoUslUnfSeq != null
						&& novoUnfSeq == null) {

					ManterAltaPedidoExameRNExceptionCode.MPM_03712
							.throwException();

				}

				if (novoIndAgenda == null) {

					ManterAltaPedidoExameRNExceptionCode.MPM_02957
							.throwException();

				}

				if (novoIndAgenda != null && !novoIndAgenda.booleanValue()
						&& novoDthrConsulta == null) {

					ManterAltaPedidoExameRNExceptionCode.MPM_02958
							.throwException();

				}

				if (novoIndAgenda != null && novoIndAgenda.booleanValue()
						&& novoDthrConsulta != null) {

					ManterAltaPedidoExameRNExceptionCode.MPM_02959
							.throwException();

				}

			} else {

				if (novoEspSeq != null) {

					ManterAltaPedidoExameRNExceptionCode.MPM_02960
							.throwException();

				}

				if (novoUslSala != null && novoUslUnfSeq != null
						&& novoUnfSeq != null) {

					ManterAltaPedidoExameRNExceptionCode.MPM_03358
							.throwException();

				}

				if (novoIndAgenda != null) {

					ManterAltaPedidoExameRNExceptionCode.MPM_02964
							.throwException();

				}

			}

		}

	}

	/**
	 * ORADB Procedure mpmk_aex_rn.rn_aexp_ver_equipe
	 * 
	 * Operação: INS, UPD
	 * 
	 * Descrição: O atributo desc equipe deverá ser nulo quando a fk EQUIPE não
	 * estiver informada. Caso a fk esteja informada ele obrigatoriamente devrá
	 * ter conteúdo.
	 * 
	 * @param {Integer} novoEqpSeq
	 * @param {String} novoDescEquipe
	 */
	public void verificarEquipe(Integer novoEqpSeq, String novoDescEquipe)
			throws ApplicationBusinessException {

		if (StringUtils.isNotBlank(novoDescEquipe) && novoEqpSeq == null) {

			// Ao informar a descricao da equipe, o código deve ser informado.
			ManterAltaPedidoExameRNExceptionCode.MPM_02674.throwException();

		}

		if (novoEqpSeq != null && StringUtils.isBlank(novoDescEquipe)) {

			// Ao informar o código da equipe,a descrição da equipe deve ser
			// informada.
			ManterAltaPedidoExameRNExceptionCode.MPM_02675.throwException();

		}

	}

	/**
	 * ORADB Procedure mpmk_aex_rn.rn_aexp_ver_unidade.
	 * 
	 * Operação: INS, UPD
	 * 
	 * Descrição: O atributo desc unidade deverá ser nulo quando a fk UNID
	 * FUNCIONAL SALA não estiver informada. Caso a fk esteja informada ele
	 * obrigatoriamente devrá ter conteúdo
	 * 
	 * @param {String} novoDescUnidade
	 * @param {Byte} novaUslSala
	 * @param {Short} novoUslUnfSeq
	 * @param {Short} novoUnfSeq
	 */
	public void verificarUnidade(String novoDescUnidade, Byte novaUslSala,
			Short novoUslUnfSeq, Short novoUnfSeq) throws ApplicationBusinessException {

		if (novoDescUnidade != null && novaUslSala == null
				&& novoUslUnfSeq == null && novoUnfSeq == null) {

			// Ao informar a descricao da unidade, os códigos devem ser
			// informados.
			ManterAltaPedidoExameRNExceptionCode.MPM_02678.throwException();

		}

		if (((novaUslSala != null && novoUslUnfSeq != null) || novoUnfSeq != null)
				&& novoDescUnidade == null) {

			// Ao informar o código da unidade,a descrição deve ser informada
			ManterAltaPedidoExameRNExceptionCode.MPM_02679.throwException();

		}

	}

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaPedidoExameDAO getAltaPedidoExameDAO() {
		return mpmAltaPedidoExameDAO;
	}

	protected MpmAltaSumarioDAO getAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}

	protected IFaturamentoApoioFacade getFaturamentoApoioFacade() {
		return this.faturamentoApoioFacade;
	}
	
}