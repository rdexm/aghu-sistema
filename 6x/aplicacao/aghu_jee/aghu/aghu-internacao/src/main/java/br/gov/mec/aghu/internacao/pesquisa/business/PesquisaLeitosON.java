package br.gov.mec.aghu.internacao.pesquisa.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioGrupoConvenioPesquisaLeitos;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.dao.AinAtendimentosUrgenciaDAO;
import br.gov.mec.aghu.internacao.dao.AinSolicTransfPacientesDAO;
import br.gov.mec.aghu.internacao.dao.VAinPesqLeitosDAO;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisaLeitosVO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PesquisaLeitosON extends BaseBusiness {

	@EJB
	private PesquisaLeitosRN pesquisaLeitosRN;

	private static final Log LOG = LogFactory.getLog(PesquisaLeitosON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO;

	@Inject
	private AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO;

	@Inject
	private VAinPesqLeitosDAO vAinPesqLeitosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3695165508335207888L;

	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum PesquisaLeitosONExceptionCode implements BusinessExceptionCode {
		FILTRO_OBRIGATORIO_PESQUISA_LEITOS, ANDAR_INVALIDO, ERRO_PARAMETRO_ANDAR_LEITO, ANDAR_LEITO_INICIAL, ANDAR_LEITO_FINAL, 
	}

	/**
	 * Método que obtém a lista de leitos.
	 * 
	 * @dbtables VAinPesqLeitos select
	 * 
	 * @param status
	 * @param acomodacao
	 * @param clinica
	 * @param convenio
	 * @param unidade
	 * @param leito
	 * @param grupoConvenio
	 * @param ala
	 * @param andar
	 * @param infeccao
	 * @return
	 */
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public List<PesquisaLeitosVO> pesquisarLeitos(
			AinTiposMovimentoLeito status, AinAcomodacoes acomodacao,
			AghClinicas clinica, FatConvenioSaude convenio,
			AghUnidadesFuncionais unidade, AinLeitos leito,
			DominioGrupoConvenioPesquisaLeitos grupoConvenio, AghAla ala,
			Integer andar, DominioSimNao infeccao,
			DominioMovimentoLeito mvtoLeito, int dataBlock,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		List<PesquisaLeitosVO> retorno = new ArrayList<PesquisaLeitosVO>();

		if (dataBlock == 1) {

			List<Object[]> res = getVAinPesqLeitosDAO()
					.pesquisaCriteriaVPL1OrdemLeito(status, acomodacao,
							clinica, convenio, unidade, leito, grupoConvenio,
							ala, andar, infeccao, firstResult, maxResult,
							orderProperty, asc);

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

			// Criando lista de VO.
			Iterator<Object[]> it = res.iterator();
			while (it.hasNext()) {
				Object[] obj = it.next();
				PesquisaLeitosVO vo = new PesquisaLeitosVO();

				if (obj[0] != null) {
					vo.setLeito((String) obj[0]);
				}
				if (obj[1] != null) {
					vo.setAla((String) obj[1]);
				}
				if (obj[2] != null) {
					vo.setClcCodigo((Byte) obj[2]);
				}
				if (obj[3] != null) {
					vo.setSigla((String) obj[3]);
				}
				if (obj[4] != null) {
					vo.setDthrLancamento(format.format((Date) obj[4]));
				}
				if (obj[5] != null) {
					vo.setNome((String) obj[5]);
				}
				if (obj[6] != null) {
					vo.setSexo(((DominioSexo) obj[6]).toString());
				}
				if (obj[7] != null) {
					String prontAux = ((Integer) obj[7]).toString();
					vo.setProntuario(prontAux.substring(0,
							prontAux.length() - 1)
							+ "/"
							+ prontAux.charAt(prontAux.length() - 1));
				}
				if (obj[8] != null) {
					vo.setGrupoMovimento((String) obj[8]);
				}
				if (obj[9] != null) {
					vo.setIntSeq(((AinInternacao) obj[9]).getSeq());
				}
				retorno.add(vo);
			}

		} else {

			List<Object[]> res = getVAinPesqLeitosDAO()
					.pesquisaCriteriaVPLOrdemLeito(status, acomodacao, clinica,
							unidade, leito, ala, andar, infeccao, firstResult,
							maxResult, orderProperty, asc);

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

			// Criando lista de VO.
			Iterator<Object[]> it = res.iterator();
			while (it.hasNext()) {
				Object[] obj = it.next();
				PesquisaLeitosVO vo = new PesquisaLeitosVO();

				if (obj[0] != null) {
					vo.setLeito((String) obj[0]);
					if (possuiAtUrgencia(vo.getLeito())) {
						vo.setAtUrgencia(true);
						vo.setInternacao(false);
					} else if (obj[11] != null) {
						vo.setIntSeq(((AinInternacao) obj[11]).getSeq());
						vo.setInternacao(true);
						vo.setAtUrgencia(false);
					} else {
						vo.setInternacao(false);
						vo.setAtUrgencia(false);
					}
				}
				if (obj[1] != null) {
					vo.setAla((String) obj[1]);
				}
				if (obj[2] != null) {
					vo.setClcCodigo((Byte) obj[2]);
				}
				if (obj[3] != null) {
					vo.setDescricaoAcomodacao((String) obj[3]);
				}
				if (obj[4] != null) {
					vo.setSexo((String) obj[4]);
				}
				if (obj[5] != null) {
					vo.setDthrLancamento(format.format((Date) obj[5]));
				}
				if (obj[6] != null) {
					vo.setGrupoMovimento((String) obj[6]);
				}
				if (obj[7] != null) {
					vo.setObservacao((String) obj[7]);
				}
				if (obj[8] != null) {
					vo.setCaracteristica((String) obj[8]);
				}
				if (obj[9] != null) {
					vo.setNome((String) obj[9]);
				}
				if (obj[10] != null) {
					vo.setDescricaoMovimentacao(((DominioMovimentoLeito) obj[10])
							.getDescricao());
				}
				retorno.add(vo);
			}
		}
		return retorno;
	}

	/**
	 * Cria a pesquisa de acordo com o Data Block STP do oracle forms
	 * 
	 * @dbtables AinSolicTransfPacientes select
	 * 
	 * @return
	 */
	@Secure("#{s:hasPermission('solicitacaoTransferenciaPaciente','pesquisar')}")
	public List<PesquisaLeitosVO> pesquisarSolicitacoesTransferenciaPacientes() {

		List<Object[]> res = this.getAinSolicTransfPacientesDAO()
				.pesquisarSolicitacoesTransferenciaPacientesOrderCriacao();

		List<PesquisaLeitosVO> retorno = new ArrayList<PesquisaLeitosVO>();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		// Criando lista de VO.
		Iterator<Object[]> it = res.iterator();
		while (it.hasNext()) {
			Object[] obj = it.next();
			PesquisaLeitosVO vo = new PesquisaLeitosVO();

			if (obj[0] != null) {
				String prontAux = ((Integer) obj[0]).toString();
				vo.setProntuario(prontAux.substring(0, prontAux.length() - 1)
						+ "/" + prontAux.charAt(prontAux.length() - 1));
			}

			if (obj[1] != null) {
				vo.setNome((String) obj[1]);
			}

			if (obj[4] != null) {
				vo.setIndLeitoIsolamento((DominioSimNao) obj[4]);
			}

			if (obj[5] != null) {
				vo.setCriadoEm((format.format((Date) obj[5])));
			}

			if (obj[6] != null) {
				vo.setDescricaoAcomodacao((String) obj[6]);
			}

			if (obj[7] != null) {
				vo.setNomeEspecialidade((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setObservacao((String) obj[8]);
			}

			vo.setDescricaoUnidade(obterDescricaoUnidade((AinLeitos) obj[2],
					(AinQuartos) obj[3], (AghUnidadesFuncionais) obj[9]));

			retorno.add(vo);
		}

		return retorno;
	}

	/**
	 * Retorna total de registros
	 * 
	 * @dbtables AinSolicTransfPacientes select
	 * 
	 * @return
	 */
	public Long pesquisarSolicitacoesTransferenciaPacientesCount() {
		return this.getAinSolicTransfPacientesDAO()
				.pesquisarSolicitacoesTransferenciaPacientesCount();
	}

	/**
	 * Retorna total de registros
	 * 
	 * @dbtables VAinPesqLeitos select
	 * 
	 * @param status
	 * @param acomodacao
	 * @param clinica
	 * @param convenio
	 * @param unidade
	 * @param leito
	 * @param grupoConvenio
	 * @param ala
	 * @param andar
	 * @param infeccao
	 * @return
	 */
	public Long pesquisarLeitosCount(AinTiposMovimentoLeito status,
			AinAcomodacoes acomodacao, AghClinicas clinica,
			FatConvenioSaude convenio, AghUnidadesFuncionais unidade,
			AinLeitos leito, DominioGrupoConvenioPesquisaLeitos grupoConvenio,
			AghAla ala, Integer andar, DominioSimNao infeccao,
			DominioMovimentoLeito mvtoLeito, int dataBlock) {

		if (dataBlock == 1) {
			return getVAinPesqLeitosDAO().pesquisaCriteriaVPL1Count(status,
					acomodacao, clinica, convenio, unidade, leito,
					grupoConvenio, ala, andar, infeccao);
		} else {
			return getVAinPesqLeitosDAO().pesquisaCriteriaVPLCount(status,
					acomodacao, clinica, convenio, unidade, leito, ala, andar,
					infeccao);
		}
	}

	/**
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @param leitoId
	 * @param quartoNumero
	 * @param unidadesFuncionais
	 * @return
	 */
	private String obterDescricaoUnidade(AinLeitos leito, AinQuartos quarto,
			AghUnidadesFuncionais unidadesFuncionais) {

		if (unidadesFuncionais != null) {
			return unidadesFuncionais.getLPADAndarAlaDescricao();
		}

		if (quarto != null && quarto.getUnidadeFuncional() != null) {
			return quarto.getUnidadeFuncional().getLPADAndarAlaDescricao();
		}

		if (leito != null && leito.getUnidadeFuncional() != null) {
			return leito.getUnidadeFuncional().getLPADAndarAlaDescricao();
		}

		return null;
	}

	/**
	 * retorna o detalhe de um atendimento de urgência.
	 * 
	 * @dbtables AinAtendimentosUrgencia select
	 * 
	 * @param codigoAtendimentoUrgencia
	 * @return
	 */
	@Secure("#{s:hasPermission('atendimentoUrgencia','pesquisar')}")
	public AinAtendimentosUrgencia obterDetalheAtendimentoUrgencia(
			Integer codigoAtendimentoUrgencia)
			throws ApplicationBusinessException {
		
		return this.getAinAtendimentosUrgenciaDAO().obterDetalheAtendimentoUrgencia(codigoAtendimentoUrgencia);
	}	

	/**
	 * 
	 * @dbtables AinAtendimentosUrgencia select
	 * 
	 * @param leito
	 * @return
	 */
	@Secure("#{s:hasPermission('atendimentoUrgencia','pesquisar')}")
	public AinAtendimentosUrgencia obterDetalheAtendimentoUrgencia2(String leito) {
		return getAinAtendimentosUrgenciaDAO()
				.obterDetalheAtendimentoUrgencia2(leito);
	}

	/**
	 * Valida se pelo menos um filtro foi informado pela pesquisa.
	 * 
	 * @param status
	 * @param acomodacao
	 * @param clinica
	 * @param convenio
	 * @param unidade
	 * @param leito
	 * @param grupoConvenio
	 * @param ala
	 * @param andar
	 * @param infeccao
	 * @throws ApplicationBusinessException
	 */
	public void validaDados(AinTiposMovimentoLeito status, AinAcomodacoes acomodacao, 
			AghClinicas clinica, FatConvenioSaude convenio, AghUnidadesFuncionais unidade, 
			AinLeitos leito, DominioGrupoConvenioPesquisaLeitos grupoConvenio, AghAla ala,
			Integer andar, DominioSimNao infeccao) throws ApplicationBusinessException {

		Integer andarLeitoIni = null;
		Integer andarLeitoFim = null;
		
		try {
		
			String andarLeitoInicial = getResourceBundleValue(PesquisaLeitosONExceptionCode.ANDAR_LEITO_INICIAL
					.toString());
			
			if (StringUtils.isNotBlank(andarLeitoInicial)) {
				andarLeitoIni = Integer.parseInt(andarLeitoInicial);
			} else {
				throw new ApplicationBusinessException(
						PesquisaLeitosONExceptionCode.ERRO_PARAMETRO_ANDAR_LEITO);
			}
			String andarLeitoFinal = getResourceBundleValue(PesquisaLeitosONExceptionCode.ANDAR_LEITO_FINAL
					.toString());
			if (StringUtils.isNotBlank(andarLeitoFinal)) {
				andarLeitoFim = Integer.parseInt(andarLeitoFinal);
			} else {
				throw new ApplicationBusinessException(
						PesquisaLeitosONExceptionCode.ERRO_PARAMETRO_ANDAR_LEITO);
			}
		} catch(NumberFormatException e) {
			throw new ApplicationBusinessException(
					PesquisaLeitosONExceptionCode.ERRO_PARAMETRO_ANDAR_LEITO);
		}
		
		if (status == null && acomodacao == null && clinica == null
				&& convenio == null && unidade == null
				&& (leito == null)
				&& grupoConvenio == null && ala == null && andar == null
				&& infeccao == null) {
			throw new ApplicationBusinessException(
					PesquisaLeitosONExceptionCode.FILTRO_OBRIGATORIO_PESQUISA_LEITOS);
		}

		if (andar != null && (andar < andarLeitoIni || andar > andarLeitoFim)) {
			throw new ApplicationBusinessException(
					PesquisaLeitosONExceptionCode.ANDAR_INVALIDO);
		}
	}


	/**
	 * Verifica qual data block deve ser executado 0 - VPL 1 - VPL1 2 - STP
	 * 
	 * @return
	 */
	public Integer verificarDataBlock(FatConvenioSaude convenio,
			DominioGrupoConvenioPesquisaLeitos grupoConvenio,
			DominioMovimentoLeito mvtoLeito) {

		return getPesquisaLeitosRN().verificarDataBlock(convenio,
				grupoConvenio, mvtoLeito);
	}

	/**
	 * Verifica se o leito possui At. Urgencia
	 * 
	 * @dbtables AinAtendimentosUrgencia select
	 * 
	 * @param leitoId
	 * @return
	 */
	public boolean possuiAtUrgencia(String leitoId) {
		return getPesquisaLeitosRN().possuiAtUrgencia(leitoId);
	}

	/**
	 * Regra de Negócio de Pesquisa Leitos
	 */
	protected PesquisaLeitosRN getPesquisaLeitosRN() {
		return pesquisaLeitosRN;
	}

	protected VAinPesqLeitosDAO getVAinPesqLeitosDAO() {
		return vAinPesqLeitosDAO;
	}

	protected AinSolicTransfPacientesDAO getAinSolicTransfPacientesDAO() {
		return ainSolicTransfPacientesDAO;
	}

	protected AinAtendimentosUrgenciaDAO getAinAtendimentosUrgenciaDAO() {
		return ainAtendimentosUrgenciaDAO;
	}
}
