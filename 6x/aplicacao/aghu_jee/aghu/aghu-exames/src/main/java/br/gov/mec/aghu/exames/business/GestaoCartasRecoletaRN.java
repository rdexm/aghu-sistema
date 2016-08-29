package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFormaIdentificacaoCaixaPostal;
import br.gov.mec.aghu.dominio.DominioSituacaoCartaColeta;
import br.gov.mec.aghu.dominio.DominioSituacaoCxtPostalServidor;
import br.gov.mec.aghu.dominio.DominioTipoMensagemExame;
import br.gov.mec.aghu.exames.dao.AelAtendimentoDiversosDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemCartasDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicCartasDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.CartaRecoletaVO;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExtratoItemCartas;
import br.gov.mec.aghu.model.AelExtratoItemCartasId;
import br.gov.mec.aghu.model.AelItemSolicCartas;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.AghCaixaPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostalServidorId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class GestaoCartasRecoletaRN extends BaseBusiness {

	@EJB
	private AelExtratoItemCartasRN aelExtratoItemCartasRN;
	
	private static final Log LOG = LogFactory.getLog(GestaoCartasRecoletaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelItemSolicCartasDAO aelItemSolicCartasDAO;
	
	@Inject
	private AelAtendimentoDiversosDAO aelAtendimentoDiversosDAO;
	
	@Inject
	private AelExtratoItemCartasDAO aelExtratoItemCartasDAO;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -468019951548200837L;

	public enum GestaoCartasRecoletaRNExceptionCode implements
	BusinessExceptionCode {
		CAMPOS_PESQUISA_VAZIOS, DATA_FIM_MENOR_DATA_INICIO, 
		AEL_02905, INFRM_DT_INICIO_E_DT_FIM;
	}

	public void validarPesquisaCartasRecoleta(DominioSituacaoCartaColeta situacao, Date dtInicio, Date dtFim,
			Integer iseSoeSeq, Integer pacCodigo) throws BaseException {
	
		if(!(situacao != null || iseSoeSeq != null || pacCodigo != null || (dtInicio != null || dtFim != null))) {
			throw new BaseException(GestaoCartasRecoletaRNExceptionCode.CAMPOS_PESQUISA_VAZIOS);
		}

		if((dtInicio!=null && dtFim==null) || (dtInicio==null && dtFim!=null)) {
			throw new BaseException(GestaoCartasRecoletaRNExceptionCode.INFRM_DT_INICIO_E_DT_FIM);
		}
		
		if(dtInicio!= null && dtFim!=null) {
			if(!DateUtil.validaDataMaior(dtFim, dtInicio)) {
				throw new BaseException(GestaoCartasRecoletaRNExceptionCode.DATA_FIM_MENOR_DATA_INICIO);
			}
			
			AghParametros dias = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DIAS_PESQUISA_CARTAS);
			if(!(DateUtil.calcularDiasEntreDatas(dtInicio, dtFim) <= dias.getVlrNumerico().intValue())) {
				throw new BaseException(GestaoCartasRecoletaRNExceptionCode.AEL_02905, dias.getVlrNumerico().intValue());
			}
		}
	}
	
	public void atualizar(AelItemSolicCartas item, String nomeMicrocomputador) throws BaseException {
		this.preAtualizar(item, nomeMicrocomputador);
		this.aelItemSolicCartasDAO.merge(item);
	}
	
	
	/**
	 * @ORADB AELT_ITE_BRU
	 */
	private void preAtualizar(AelItemSolicCartas item, String nomeMicrocomputador) throws BaseException {
		item.setAlteradoEm(new Date());
		// -- se foi informado um motivo de retorno atualiza situação para RE
		if(item.getMotivoRetorno() != null) {
			item.setSituacao(DominioSituacaoCartaColeta.RE);
			// -- envia msg cx postal solicitante
			this.rnItepReCxPostal(item);
			//-- cancela exame
			this.rnItepReCancExa(item, nomeMicrocomputador);
		}
		this.rnItepAtuExtrato(item);
	}

	
	public List<CartaRecoletaVO> obterCartaParaImpressao(Short iseSeqp, Integer iseSoeSeq, Short seqp) {
		List<CartaRecoletaVO> cartas = 	getAelItemSolicCartasDAO().obterCartaParaImpressao(iseSeqp, iseSoeSeq, seqp);
		for(CartaRecoletaVO carta : cartas) {
			carta.setNome(aelcGetNomeCarta(iseSoeSeq));
			carta.setProntuario(aelcGetProntCarta(iseSoeSeq));
			carta.setEndereco(aelcGetEndCarta(iseSoeSeq));
			carta.setDescJejum(carta.getJejum() != null ? "Jejum: " + carta.getJejum() + " horas." : "Não necessita Jejum.");
		}
		return cartas;
	}
	
	/**
	 * @ORADB AELC_GET_PRONT_CARTA
	 */
	public Integer aelcGetProntCarta(Integer seq) {
		AelSolicitacaoExames sol = getSolicitacaoExameFacade().obterSolicitacaoExame(seq);
		if(sol.getAtendimento() != null && sol.getAtendimento().getPaciente() != null) {
			return sol.getAtendimento().getPaciente().getProntuario(); 
		}
		else if(sol.getAtendimentoDiverso() != null && sol.getAtendimentoDiverso().getAipPaciente() != null) {
			return sol.getAtendimentoDiverso().getAipPaciente().getProntuario();
		}
		return null;
	}

	/**
	 * @ORADB AELC_GET_NOME_CARTA
	 */
	public String aelcGetNomeCarta(Integer seq) {
		AelSolicitacaoExames sol = getSolicitacaoExameFacade().obterSolicitacaoExame(seq);
		if(sol.getAtendimento() != null && sol.getAtendimento().getPaciente() != null) {
			return "Sr(a). " + WordUtils.capitalizeFully(sol.getAtendimento().getPaciente().getNome()); 
		}
		else if(sol.getAtendimentoDiverso() != null) {
			return WordUtils.capitalizeFully(this.obterNomeAtendDiv(sol.getAtendimentoDiverso().getSeq()));
		}
		return null;
	}

	/**
	 * @ORADB AELC_GET_END_CARTA
	 */
	public String aelcGetEndCarta(Integer seq) {
		AelSolicitacaoExames sol = getSolicitacaoExameFacade().obterSolicitacaoExame(seq);
		
		StringBuffer endereco = new StringBuffer(1000);
		Integer pacCodigo = null;
		Integer laeSeq = null;
		Integer cadSeq = null;
		
		if(sol.getAtendimento() != null && sol.getAtendimento().getPaciente() != null) {
			pacCodigo = sol.getAtendimento().getPaciente().getCodigo(); 
		}
		else if(sol.getAtendimentoDiverso() != null) {
			if(sol.getAtendimentoDiverso().getAipPaciente() != null) {
				pacCodigo = sol.getAtendimentoDiverso().getAipPaciente().getCodigo();	
			}
			else if(sol.getAtendimentoDiverso().getAelLaboratorioExternos() != null) {
				laeSeq = sol.getAtendimentoDiverso().getAelLaboratorioExternos().getSeq(); 
			}
			else if(sol.getAtendimentoDiverso().getAbsCandidatosDoadores() != null) {
				cadSeq = sol.getAtendimentoDiverso().getAbsCandidatosDoadores().getSeq(); 
			}
		}

		if(pacCodigo != null) {
			VAipEnderecoPaciente vAipEnd = getCadastroPacienteFacade().obterEndecoPaciente(pacCodigo);
			if(vAipEnd != null) {
				endereco.append(WordUtils.capitalizeFully(vAipEnd.getLogradouro()));
				endereco.append(' ');
				endereco.append(vAipEnd.getNroLogradouro());
				if(!StringUtils.isEmpty(vAipEnd.getComplLogradouro())) {
					endereco.append('/');
					endereco.append(WordUtils.capitalizeFully(vAipEnd.getComplLogradouro()));
				}
				if(!StringUtils.isEmpty(vAipEnd.getBairro())) {
					endereco.append('\n');
					endereco.append("Bairro: ");
					endereco.append(WordUtils.capitalizeFully(vAipEnd.getBairro()));
				}
				if(!StringUtils.isEmpty(vAipEnd.getCidade())) {
					endereco.append('\n');
					endereco.append(WordUtils.capitalizeFully(vAipEnd.getCidade()));
				}
				if(!StringUtils.isEmpty(vAipEnd.getUf())) {
					endereco.append(',');
					endereco.append(' ');
					endereco.append(vAipEnd.getUf());
				}
				if(vAipEnd.getCep() != null) {
					endereco.append('\n');
					endereco.append(vAipEnd.getCep().longValue());
				}
			}
		}
		else if(laeSeq != null) {
			endereco.append(WordUtils.capitalizeFully(sol.getAtendimentoDiverso().getAelLaboratorioExternos().getEndereco()));
			if(!StringUtils.isEmpty(sol.getAtendimentoDiverso().getAelLaboratorioExternos().getCidade())) {
				endereco.append('\n');
				endereco.append(WordUtils.capitalizeFully(sol.getAtendimentoDiverso().getAelLaboratorioExternos().getCidade()));
			}
			if(sol.getAtendimentoDiverso().getAelLaboratorioExternos().getCep() != null) {
				endereco.append('\n');
				endereco.append(sol.getAtendimentoDiverso().getAelLaboratorioExternos().getCep().longValue());
			}
		}
		else if(cadSeq != null) {
			endereco.append(WordUtils.capitalizeFully(StringUtils.capitalize(sol.getAtendimentoDiverso().getAbsCandidatosDoadores().getLogradouro())));
			endereco.append(' ');
			endereco.append(sol.getAtendimentoDiverso().getAbsCandidatosDoadores().getNroLogradouro());
			if(!StringUtils.isEmpty(sol.getAtendimentoDiverso().getAbsCandidatosDoadores().getComplLogradouro())) {
				endereco.append('/');
				endereco.append(WordUtils.capitalizeFully(sol.getAtendimentoDiverso().getAbsCandidatosDoadores().getComplLogradouro()));
			}
			if(!StringUtils.isEmpty(sol.getAtendimentoDiverso().getAbsCandidatosDoadores().getBairro())) {
				endereco.append('\n');
				endereco.append("Bairro: ");
				endereco.append(WordUtils.capitalizeFully(sol.getAtendimentoDiverso().getAbsCandidatosDoadores().getBairro()));
			}
			if(sol.getAtendimentoDiverso().getAbsCandidatosDoadores().getCep() != null) {
				endereco.append('\n');
				endereco.append(sol.getAtendimentoDiverso().getAbsCandidatosDoadores().getCep().longValue());
			}
		}
		
		return endereco.toString();
	}
	
	
	/**
	 * @ORADB AELK_ITEP_RN.RN_ITEP_ATU_EXTRATO
	 */
	private void rnItepAtuExtrato(AelItemSolicCartas item) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		/* Atualiza o extrato item solic cartas	no insert e em cada update que mudar a situação
		Atualiza também o Retorno, cfme informado */
		Short seqp = 0;		
		
		for(AelExtratoItemCartas extrato : aelExtratoItemCartasDAO.buscarAelExtratoItemCartasPorItemCartas(item)) {
			if(extrato.getId().getSeqp() > seqp) {
				seqp = extrato.getId().getSeqp();
			}
		}
		
		seqp++;
		AelExtratoItemCartas extrato = new AelExtratoItemCartas();
		extrato.setId(new AelExtratoItemCartasId(item.getId().getIseSoeSeq(), item.getId().getIseSeqp(), item.getId().getSeqp(), seqp));
		extrato.setDthrEvento(new Date());
		extrato.setServidor(servidorLogado);
		extrato.setSituacao(item.getSituacao());
		extrato.setMotivoRetorno(item.getMotivoRetorno());
		getAelExtratoItemCartasRN().inserirAelExtratoItemCartas(extrato, false);
	}
	
	
	
	/**
	 * @ORADB AELK_ITEP_RN.RN_ITEP_RE_CANC_EXA
	 */
	private void rnItepReCancExa(AelItemSolicCartas item, String nomeMicrocomputador) throws BaseException {
		/* Verifica se o retorno informado deve cancelar o exame solicitado
		   e cancela o exame */
		if(item.getMotivoRetorno() != null && Boolean.TRUE.equals(item.getMotivoRetorno().getIndCancelaExame())) {
			/* Busca a situacao Cancelado*/
			AghParametros situacao =  getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
			AghParametros motivo =  getParametroFacade().obterAghParametro(AghuParametrosEnum.P_MOC_CANC_RETORNO_CARTA);
			
			AelSitItemSolicitacoes sit = getSolicitacaoExameFacade().obterAelSitItemSolicitacoes(situacao.getVlrTexto());
			AelMotivoCancelaExames mot = getSolicitacaoExameFacade().obterAelMotivoCancelaExames(motivo.getVlrNumerico().shortValue());
			
			item.getItemSolicitacaoExame().setSituacaoItemSolicitacao(sit);
			item.getItemSolicitacaoExame().setAelMotivoCancelaExames(mot);
			getSolicitacaoExameFacade().atualizarSemFlush(item.getItemSolicitacaoExame(), nomeMicrocomputador, true);
		}
	}

	
	/**
	 * @ORADB AELK_ITEP_RN.RN_ITEP_RE_CX_POSTAL
	 */
	private void rnItepReCxPostal(AelItemSolicCartas item) throws BaseException {
		//-- busca as informações relacionadas ao retorno
		if(item.getMotivoRetorno() != null && Boolean.TRUE.equals(item.getMotivoRetorno().getIndAvisaSolicitante())) {
			String descricaoExame = null;
			String nome = null;
			Integer prontuario = null;
			RapServidores solicitante = null;
			StringBuffer texto = new StringBuffer(2000);
			
			// -- busca a descrição do exame
			descricaoExame = item.getItemSolicitacaoExame().getExame().getDescricaoUsual();
			// -- busca solicitante e pacinte
			if(item.getSolicitacaoExame().getAtendimento() != null) {
				solicitante = item.getSolicitacaoExame().getServidor();
				nome = item.getSolicitacaoExame().getAtendimento().getPaciente().getNome();
				prontuario = item.getSolicitacaoExame().getAtendimento().getPaciente().getProntuario();
			}
			else {
				solicitante = item.getSolicitacaoExame().getServidor();
				nome = obterNomeAtendDiv(item.getSolicitacaoExame().getAtendimentoDiverso().getSeq());
			}
			
			texto.append("Carta para recoleta de ")
			.append(nome);
			if(prontuario != null) {
				texto.append(", prontuário: ");
				texto.append(prontuario);
			}
			texto.append(", retornou pelo motivo: " + StringUtils.capitalize(item.getMotivoRetorno().getDescricao()) + "\n")
			.append("Solicitação: " + item.getId().getIseSoeSeq() + ", exame"+((item.getMotivoRetorno().getIndCancelaExame())?"CANCELADO":"")+": "+descricaoExame+".");
			
			//--inclui mensagem na caixa postal
			AghCaixaPostal caixaPostal = new AghCaixaPostal(); 
			caixaPostal.setDthrInicio(new Date());
			caixaPostal.setTipoMensagem(DominioTipoMensagemExame.I);
			caixaPostal.setAcaoObrigatoria(false);
			caixaPostal.setMensagem(texto.toString());
			caixaPostal.setCriadoEm(new Date());
			caixaPostal.setFormaIdentificacao(DominioFormaIdentificacaoCaixaPostal.E);
			getAghuFacade().persistirAghCaixaPostal(caixaPostal);
			
			// -- Atribui mensagem à caixa postal do solicitante do exame
			AghCaixaPostalServidor caixaPostalServidor = new AghCaixaPostalServidor();
			caixaPostalServidor.setId(new AghCaixaPostalServidorId(caixaPostal.getSeq(), solicitante));
			caixaPostalServidor.setSituacao(DominioSituacaoCxtPostalServidor.N);
			getAghuFacade().persistirAghCaixaPostalServidor(caixaPostalServidor);
		}
		
	}
	
	/**
	 * @ORADB AELC_NOME_ATEND_DIV
	 */
	public String obterNomeAtendDiv(Integer atdDivseq) {
		AelAtendimentoDiversos atdDiv =  getAelAtendimentoDiversosDAO().obterPorChavePrimaria(atdDivseq);
		if(atdDiv.getAipPaciente() != null) {
			return atdDiv.getAipPaciente().getNome(); 
		}
		else if(atdDiv.getAelProjetoPesquisas() != null) {
			return atdDiv.getAelProjetoPesquisas().getNumero() + " - " + atdDiv.getAelProjetoPesquisas().getNome();
		}
		else if(atdDiv.getAelCadCtrlQualidades() != null) {
			return atdDiv.getAelCadCtrlQualidades().getMaterial();
		}
		else if(atdDiv.getAelLaboratorioExternos() != null) {
			return atdDiv.getAelLaboratorioExternos().getNome();
		}
		else if(atdDiv.getAelDadosCadaveres() != null) {
			return atdDiv.getAelDadosCadaveres().getNome();
		}
		else if(atdDiv.getAbsCandidatosDoadores() != null) {
			return atdDiv.getAbsCandidatosDoadores().getNome();
		}
		return null;
	}

	private AelAtendimentoDiversosDAO getAelAtendimentoDiversosDAO() {
		return aelAtendimentoDiversosDAO;
	}

	private AelExtratoItemCartasRN getAelExtratoItemCartasRN() {
		return aelExtratoItemCartasRN;
	}
	
	private AelItemSolicCartasDAO getAelItemSolicCartasDAO() {
		return aelItemSolicCartasDAO;
	}

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return this.cadastroPacienteFacade;
	}

	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
