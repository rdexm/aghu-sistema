package br.gov.mec.aghu.exames.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioProgramacaoExecExames;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaIdade;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelEquipamentosDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicHistDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicExameHistDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMetodoDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelValorNormalidCampoDAO;
import br.gov.mec.aghu.exames.vo.NormalidadeHistoricoVO;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AelExtratoItemSolicHist;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AelValorNormalidCampo;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class MascaraExamesRN extends BaseBusiness {
	
	private static final String _BARRA_P_ = "</p>";

	private static final String _P_ = "<p>";
	
	private static final String _RECEBIDO_EM_ = " Recebido em: ";

	private static final String MATERIAL_ = "Material ";

	@Inject
	private AelExtratoItemSolicHistDAO aelExtratoItemSolicHistDAO;
	
	private static final Log LOG = LogFactory.getLog(MascaraExamesRN.class);

	@EJB
	private IAghuFacade aghuFacade;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AelMetodoDAO aelMetodoDAO;
	
	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;
	
	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelItemSolicExameHistDAO aelItemSolicExameHistDAO;
	
	@Inject
	private AelValorNormalidCampoDAO aelValorNormalidCampoDAO;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AelEquipamentosDAO aelEquipamentosDAO;

	private static final long serialVersionUID = -7469645451135388246L;

	/**
	 * ORADB AELP_GET_MASC_EQUIP
	 * 
	 * @HIST MascaraExamesRN.buscaInformacaoEquipamentoHist
	 * @param solicitacaoExameSeq
	 * @param itemSolicitacaoExameSeq
	 * @return
	 */
	public String buscaInformacaoEquipamento(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) {
		StringBuffer linhaRetorno = new StringBuffer();
		String equDesc = getAelAmostraItemExamesDAO().buscarAelAmostraItemExamesPorItemSolicitacaoExame(solicitacaoExameSeq, itemSolicitacaoExameSeq);

		if(equDesc != null){
			return "Equipamento: " + equDesc;
		}


		AelItemSolicitacaoExames item = getAelItemSolicitacaoExameDAO().obterPorId(solicitacaoExameSeq, itemSolicitacaoExameSeq);
		if(item!=null){

			AelSolicitacaoExames solicitacaoExame = item.getSolicitacaoExame()!=null?item.getSolicitacaoExame():null;

			DominioProgramacaoExecExames programacao = null;
			boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(solicitacaoExame.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.AUTOMACAO_ROTINA);
			if(solicitacaoExame.getUnidadeFuncional() != null && possuiCaracteristica){
				programacao = DominioProgramacaoExecExames.R;
			}else{
				programacao = DominioProgramacaoExecExames.U;
			}

			List<AelEquipamentos> equipamentos = getAelEquipamentosDAO().pesquisaEquipamentosPorEmaExaSiglaManSeq(item.getExame().getSigla(), item.getMaterialAnalise().getSeq(), programacao);
			if(equipamentos != null && equipamentos.size() > 0){
				linhaRetorno.append("Equipamento: ").append(equipamentos.get(0).getDescricao());
			}

		}
		return linhaRetorno.toString();
	}

	/**
	 * ORADB AELP_GET_MASC_METODO
	 * 
	 * @HIST MascaraExamesRN.buscaInformacaoMetodoHist
	 * @param solicitacaoExameSeq
	 * @param itemSolicitacaoExameSeq
	 * @return
	 * @throws BaseException 
	 */
	public String buscaInformacaoMetodo(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException {
		AghParametros parametroAreaExec = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);

		AelExtratoItemSolicitacao itemExtrato = getAelExtratoItemSolicitacaoDAO().obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntrada(solicitacaoExameSeq, itemSolicitacaoExameSeq, parametroAreaExec.getVlrTexto());

		if(itemExtrato != null){
			String metodo = getAelMetodoDAO().obterMetodoAalisePorSoeSeqEDataEvento(solicitacaoExameSeq, itemSolicitacaoExameSeq, itemExtrato.getDataHoraEvento());

			if(metodo != null){
				return "Método: " + metodo;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}


	/**
	 * ORADB AELP_GET_MASC_HISTOR
	 * 
	 * @param solicitacaoExameSeq
	 * @param itemSolicitacaoExameSeq
	 * @param campoLaudoSeq
	 * @return
	 * @throws BaseException 
	 */
	public List<String> buscaInformacaoHistorico(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq, Integer campoLaudoSeq) throws BaseException {

		if(campoLaudoSeq == null || campoLaudoSeq.intValue()==0){
			return null;
		}

		AelItemSolicitacaoExames item = getAelItemSolicitacaoExameDAO().obterPorId(solicitacaoExameSeq, itemSolicitacaoExameSeq);
		AelSolicitacaoExames solicitacaoExame = item.getSolicitacaoExame();
		List<String> listaRestorno = new ArrayList<String>(); 

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));

		//Tabelas foram mudadas de histórico para concretas
		AelAtendimentoDiversos atendimentoDiverso = solicitacaoExame.getAtendimentoDiverso();
		AghAtendimentos atendimento = solicitacaoExame.getAtendimento();
		AipPacientes paciente = null;

		if(atendimento != null){
			paciente = atendimento.getPaciente();
		}else{
			paciente = atendimentoDiverso.getAipPaciente();
		}


		AghParametros parametroAreaExec = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		AghParametros parametroSitLiberado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros parametroNumResultAnterior = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_NRO_RESULTADOS_ANTERIORES);
		/*
		int v_soe_seq_ant = 0;
        short v_seqp_ant = 0;		
		 */
		List<NormalidadeHistoricoVO> listNorms = getAelValorNormalidCampoDAO().obterValoresNormalidadeHistorico(paciente.getCodigo(), 
				solicitacaoExameSeq, 
				parametroSitLiberado.getVlrTexto(), 
				parametroAreaExec.getVlrTexto(), 
				item.getExame().getSigla(), 
				item.getMaterialAnalise().getSeq(), 
				campoLaudoSeq, 
				item.getDthrLiberada(),
				parametroNumResultAnterior.getVlrNumerico().intValue());

		for (NormalidadeHistoricoVO nornalidadeHistoricoVO : listNorms) {
			/*
        	if(	nornalidadeHistoricoVO.getSoeSeq().intValue() != v_soe_seq_ant 
        		|| nornalidadeHistoricoVO.getSeqp().shortValue() != v_seqp_ant){

        		v_soe_seq_ant = nornalidadeHistoricoVO.getSoeSeq().intValue();
                v_seqp_ant    = nornalidadeHistoricoVO.getSeqp().shortValue();
        	}
			 */

			StringBuffer linhaRetorno = new StringBuffer();

			StringBuffer resultado = new StringBuffer();
			resultado.append(nornalidadeHistoricoVO.getResultado());
			if(resultado.indexOf(",") == 0) {
				resultado.insert(0, "0");
			}

			linhaRetorno.append(sdf.format(nornalidadeHistoricoVO.getDataEvento()))
			.append("  ")
			.append(resultado)
			.append("  ")
			.append(nornalidadeHistoricoVO.getDescricao());			

			listaRestorno.add(linhaRetorno.toString());
		}

		return listaRestorno;
	}

	/**
	 * ORADB AELP_GET_MASC_HISTOR
	 * 
	 * @param solicitacaoExameSeq
	 * @param itemSolicitacaoExameSeq
	 * @param campoLaudoSeq
	 * @return
	 * @throws BaseException 
	 */
	public List<String> buscaInformacaoHistoricoHist(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq, Integer campoLaudoSeq) throws BaseException {

		if(campoLaudoSeq == null || campoLaudoSeq.intValue()==0){
			return null;
		}

		AelItemSolicExameHist item = getAelItemSolicExameHistDAO().obterPorId(solicitacaoExameSeq, itemSolicitacaoExameSeq);
		AelSolicitacaoExamesHist solicitacaoExame = item.getSolicitacaoExame();
		List<String> listaRestorno = new ArrayList<String>(); 

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));

		//Tabelas foram mudadas de histórico para concretas
		AelAtendimentoDiversos atendimentoDiverso = solicitacaoExame.getAtendimentoDiverso();
		AghAtendimentos atendimento = solicitacaoExame.getAtendimento();
		AipPacientes paciente = null;

		if(atendimento != null){
			paciente = atendimento.getPaciente();
		}else{
			paciente = atendimentoDiverso.getAipPaciente();
		}


		AghParametros parametroAreaExec = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		AghParametros parametroSitLiberado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros parametroNumResultAnterior = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_NRO_RESULTADOS_ANTERIORES);
		/*
		int v_soe_seq_ant = 0;
        short v_seqp_ant = 0;		
		 */
		List<NormalidadeHistoricoVO> listNorms = getAelValorNormalidCampoDAO().obterValoresNormalidadeHistorico(paciente.getCodigo(), 
				solicitacaoExameSeq, 
				parametroSitLiberado.getVlrTexto(), 
				parametroAreaExec.getVlrTexto(), 
				item.getExame().getSigla(), 
				item.getMaterialAnalise().getSeq(), 
				campoLaudoSeq, 
				item.getDthrLiberada(),
				parametroNumResultAnterior.getVlrNumerico().intValue());

		for (NormalidadeHistoricoVO nornalidadeHistoricoVO : listNorms) {
			/*
        	if(	nornalidadeHistoricoVO.getSoeSeq().intValue() != v_soe_seq_ant 
        		|| nornalidadeHistoricoVO.getSeqp().shortValue() != v_seqp_ant){

        		v_soe_seq_ant = nornalidadeHistoricoVO.getSoeSeq().intValue();
                v_seqp_ant    = nornalidadeHistoricoVO.getSeqp().shortValue();
        	}
			 */

			StringBuffer linhaRetorno = new StringBuffer();

			StringBuffer resultado = new StringBuffer();
			resultado.append(nornalidadeHistoricoVO.getResultado());
			if(resultado.indexOf(",") == 0) {
				resultado.insert(0, "0");
			}

			linhaRetorno.append(sdf.format(nornalidadeHistoricoVO.getDataEvento()))
			.append("  ")
			.append(resultado)
			.append("  ")
			.append(nornalidadeHistoricoVO.getDescricao());			

			listaRestorno.add(linhaRetorno.toString());
		}

		return listaRestorno;
	}



	/**
	 * ORADB AELP_GET_MASC_METODO
	 * 
	 * @param solicitacaoExameSeq
	 * @param itemSolicitacaoExameSeq
	 * @return
	 * @throws BaseException 
	 */
	public String buscaInformacaoMetodoHist(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException {
		AghParametros parametroAreaExec = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);

		AelExtratoItemSolicHist itemExtrato = getAelExtratoItemSolicHistDAO().obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntradaHist(solicitacaoExameSeq, itemSolicitacaoExameSeq, parametroAreaExec.getVlrTexto());

		if(itemExtrato != null){
			String metodo = getAelMetodoDAO().obterMetodoAalisePorSoeSeqEDataEventoHist(solicitacaoExameSeq, itemSolicitacaoExameSeq, itemExtrato.getDataHoraEvento());

			if(metodo != null){
				return "Método: " + metodo;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}

	/**
	 * ORADB AELP_GET_MASC_DT_REC
	 * 
	 * @HIST MascaraExamesRN.buscaInformacaoRecebimentoHist
	 * @param solicitacaoExameSeq
	 * @param itemSolicitacaoExameSeq
	 * @return
	 *  
	 */
	public String buscaInformacaoRecebimento(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException{
		AelItemSolicitacaoExames item = getAelItemSolicitacaoExameDAO().obterPorId(solicitacaoExameSeq, itemSolicitacaoExameSeq);

		AghParametros parametroAreaExec = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		AghParametros parametroCodMatDiversos = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MATERIAIS_DIVERSOS);

		AelExtratoItemSolicitacao itemExtrato = getAelExtratoItemSolicitacaoDAO().obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntrada(solicitacaoExameSeq, itemSolicitacaoExameSeq, parametroAreaExec.getVlrTexto());
		if(itemExtrato != null){
			if(item.getMaterialAnalise().getSeq().equals(parametroCodMatDiversos.getVlrNumerico().intValue())){
				String descMatAnal = item.getDescMaterialAnalise()!= null && item.getDescMaterialAnalise().trim().length() > 60 ? item.getDescMaterialAnalise().substring(1, 60):item.getDescMaterialAnalise(); 
				return MATERIAL_ + descMatAnal; 
			}else{
				return MATERIAL_ + item.getMaterialAnalise().getDescricao();
			}
		}else{
			return null;
		}
	}

	/**
	 * ORADB AELP_GET_MASC_DT_REC
	 * 
	 * @param solicitacaoExameSeq
	 * @param itemSolicitacaoExameSeq
	 * @return
	 *  
	 */
	public String buscaInformacaoRecebimentoHist(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
		AelItemSolicExameHist item = getAelItemSolicExameHistDAO().obterPorId(solicitacaoExameSeq, itemSolicitacaoExameSeq);

		AghParametros parametroAreaExec = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		AghParametros parametroCodMatDiversos = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MATERIAIS_DIVERSOS);

		AelExtratoItemSolicHist itemExtrato = getAelExtratoItemSolicHistDAO().obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntradaHist(solicitacaoExameSeq, itemSolicitacaoExameSeq, parametroAreaExec.getVlrTexto());
		if(itemExtrato != null){
			if(item.getMaterialAnalise().getSeq().equals(parametroCodMatDiversos.getVlrNumerico().intValue())){
				String descMatAnal = item.getDescMaterialAnalise()!= null && item.getDescMaterialAnalise().trim().length() > 60 ? item.getDescMaterialAnalise().substring(1, 60):item.getDescMaterialAnalise(); 
				return MATERIAL_ + descMatAnal + _RECEBIDO_EM_+ sdf.format(itemExtrato.getDataHoraEvento()); 
			}else{
				return MATERIAL_ + item.getMaterialAnalise().getDescricao() + _RECEBIDO_EM_+ sdf.format(itemExtrato.getDataHoraEvento());
			}
		}else{
			return null;
		}
	}


	/**
	 * ORADB AELP_GET_MASC_VALREF
	 * 
	 * @param solicitacaoExameSeq
	 * @param itemSolicitacaoExameSeq
	 * @param campoLaudoSeq
	 * @return
	 * @throws BaseException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String buscaInformacaoValoresReferencia(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq, AelParametroCamposLaudo campo) throws BaseException {
		AelSolicitacaoExames solicitacaoExame = getAelSolicitacaoExameDAO().obterPeloId(solicitacaoExameSeq);
		StringBuffer linhaRetorno = new StringBuffer();

		if(solicitacaoExame != null){
			AelAtendimentoDiversos atendimentoDiverso = solicitacaoExame.getAtendimentoDiverso();
			AghAtendimentos atendimento = solicitacaoExame.getAtendimento();

			if(atendimentoDiverso != null){
				if(atendimentoDiverso.getOrigemAmostra() != null && atendimentoDiverso.getOrigemAmostra().equals("N")){
					return "";
				}else{
					if(atendimentoDiverso.getAipPaciente() == null){
						if(atendimentoDiverso.getSexo() == null || atendimentoDiverso.getDtNascimento() == null){
							return "Valor de referência não informado por falta de dados cadastrais do paciente.";
						}
					}
				}
			}

			AghParametros parametroAreaExec = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
			AghParametros parametroDtNovaMasc = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DATA_IMPL_NOVA_MASCARA);

			AelExtratoItemSolicitacao itemExtrato = getAelExtratoItemSolicitacaoDAO().obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntrada(solicitacaoExameSeq, itemSolicitacaoExameSeq, parametroAreaExec.getVlrTexto());

			if(itemExtrato != null){
				if(CoreUtil.isMenorDatas(itemExtrato.getDataHoraEvento(), parametroDtNovaMasc.getVlrData())){
					return "";
				}

				DominioSexo sexoPaciente = null;
				Date dtNascimento = null;

				if(atendimento != null){
					sexoPaciente = atendimento.getPaciente().getSexo();
					dtNascimento = atendimento.getPaciente().getDtNascimento();
				}else{
					sexoPaciente = atendimentoDiverso.getAipPaciente().getSexo();
					dtNascimento = atendimentoDiverso.getAipPaciente().getDtNascimento();
				}

				Integer idadeAnos = DateUtil.obterQtdAnosEntreDuasDatas(dtNascimento, itemExtrato.getDataHoraEvento());
				Integer idadeMeses = DateUtil.obterQtdMesesEntreDuasDatas(dtNascimento, itemExtrato.getDataHoraEvento());
				Integer idadeDias = DateUtil.obterQtdDiasEntreDuasDatas(dtNascimento, itemExtrato.getDataHoraEvento());

				List<AelValorNormalidCampo> normalidades = getAelValorNormalidCampoDAO().buscaNormalidadeCampoLaudoSemIdade(campo.getId().getCalSeq(), itemExtrato.getDataHoraEvento(), sexoPaciente); 

				if(campo.getCampoLaudoRelacionado() != null && (normalidades == null || normalidades.size() == 0)) {
					normalidades = getAelValorNormalidCampoDAO().buscaNormalidadeCampoLaudoSemIdade(campo.getCampoLaudoRelacionado().getSeq(), itemExtrato.getDataHoraEvento(), sexoPaciente);				
				}

				if(normalidades==null || normalidades.size() == 0){
					return "";
				}else{
					String textoLivre = campo.getTextoLivre();
					
					if (textoLivre.startsWith("<p>")) {
						// Remove o <p>
						textoLivre = textoLivre.substring(3);
					}
					
					if (textoLivre.endsWith("</p>")) {
						// Remove o </p>
						textoLivre = textoLivre.substring(0, textoLivre.length() - 4);
					}
					
					linhaRetorno.append(textoLivre).append(' ');					
				}

				for (AelValorNormalidCampo normalidade : normalidades) {

					String descricaoNormalidade = (normalidade.getUnidadeMedida()!=null && normalidade.getUnidadeMedida().getDescricao()!=null)?normalidade.getUnidadeMedida().getDescricao():"";

					/*Valores minimos*/
					Double v_valor_minimo_num = null;
					String v_valor_minimo_txt = null;

					/*Valores máximos*/
					Double v_valor_maximo_num = null;
					String v_valor_maximo_txt = null;

					if(CoreUtil.isNumeroInteger(normalidade.getValorMinimo())){
						if(normalidade.getQtdeCasasDecimais()==null || normalidade.getQtdeCasasDecimais().shortValue()==0){
							v_valor_minimo_txt = normalidade.getValorMinimo().toString();
						}else{
							v_valor_minimo_num = (new Double(normalidade.getValorMinimo()) / Math.pow(new Double(10), (normalidade.getQtdeCasasDecimais()!=null)?normalidade.getQtdeCasasDecimais():0));
							v_valor_minimo_txt = v_valor_minimo_num.toString();	
						}

					}else{
						v_valor_minimo_txt = normalidade.getValorMinimo();
					}

					if(CoreUtil.isNumeroInteger(normalidade.getValorMaximo())){
						if(normalidade.getQtdeCasasDecimais()==null || normalidade.getQtdeCasasDecimais().shortValue()==0){
							v_valor_maximo_txt = normalidade.getValorMaximo().toString();
						}else{
							v_valor_maximo_num = (new Double(normalidade.getValorMaximo()) / Math.pow(new Double(10), (normalidade.getQtdeCasasDecimais()!=null)?normalidade.getQtdeCasasDecimais():0));
							v_valor_maximo_txt = v_valor_maximo_num.toString();	
						}
					}else{
						v_valor_maximo_txt = normalidade.getValorMaximo();
					}

					if(normalidade.getIdadeMinima() == null && normalidade.getIdadeMaxima() == null){
						montaDescricaoReferencia(linhaRetorno, v_valor_minimo_txt, v_valor_maximo_txt, descricaoNormalidade);

					}else if(DominioUnidadeMedidaIdade.D.equals(normalidade.getUnidMedidaIdade())){
						if(idadeAnos.intValue()==0 && idadeMeses.intValue()==0 && normalidade.getIdadeMinima()!=null && normalidade.getIdadeMaxima()!=null
								&& CoreUtil.isBetweenRange(idadeDias, normalidade.getIdadeMinima().intValue(), normalidade.getIdadeMaxima().intValue())){
							montaDescricaoReferencia(linhaRetorno, v_valor_minimo_txt, v_valor_maximo_txt, descricaoNormalidade);
						}

					}else if(DominioUnidadeMedidaIdade.M.equals(normalidade.getUnidMedidaIdade())){
						if(idadeAnos.intValue()==0 && normalidade.getIdadeMinima()!=null && normalidade.getIdadeMaxima()!=null
								&& CoreUtil.isBetweenRange(idadeMeses, normalidade.getIdadeMinima().intValue(), normalidade.getIdadeMaxima().intValue())){
							montaDescricaoReferencia(linhaRetorno, v_valor_minimo_txt, v_valor_maximo_txt, descricaoNormalidade);
						}

					}else if(DominioUnidadeMedidaIdade.A.equals(normalidade.getUnidMedidaIdade())){
						if(normalidade.getIdadeMinima()!=null && normalidade.getIdadeMaxima()!=null
								&& CoreUtil.isBetweenRange(idadeAnos, normalidade.getIdadeMinima().intValue(), normalidade.getIdadeMaxima().intValue())){
							montaDescricaoReferencia(linhaRetorno, v_valor_minimo_txt, v_valor_maximo_txt, descricaoNormalidade);
						}
					}
				}
			}
		}
		return StringEscapeUtils.unescapeHtml4(linhaRetorno.toString());
	}
	
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public String buscaInformacaoValoresReferenciaModeloAntigo(Integer solicitacaoExameSeq,
			Short itemSolicitacaoExameSeq, AelParametroCamposLaudo campo) throws BaseException {
		StringBuffer linhaRetorno = new StringBuffer();

		AghParametros parametroAreaExec = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);

		AelExtratoItemSolicitacao itemExtrato = getAelExtratoItemSolicitacaoDAO()
				.obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntrada(solicitacaoExameSeq,
						itemSolicitacaoExameSeq, parametroAreaExec.getVlrTexto());

		if (itemExtrato != null) {

			List<AelValorNormalidCampo> normalidades = getAelValorNormalidCampoDAO()
					.buscaNormalidadeCampoLaudoPorData(campo.getId().getCalSeq(), itemExtrato.getDataHoraEvento());

			if (campo.getCampoLaudoRelacionado() != null && (normalidades == null || normalidades.size() == 0)) {
				normalidades = getAelValorNormalidCampoDAO().buscaNormalidadeCampoLaudoPorData(
						campo.getCampoLaudoRelacionado().getSeq(), itemExtrato.getDataHoraEvento());
			}

			if (normalidades == null || normalidades.size() == 0) {
				return "";
			} else {
				String textoLivre = campo.getTextoLivre();
				if (textoLivre == null) {
					textoLivre = "";
				}
				if (textoLivre.startsWith(_P_)) {
					// Remove o <p>
					textoLivre = textoLivre.substring(3);
				}

				if (textoLivre.endsWith(_BARRA_P_)) {
					// Remove o </p>
					textoLivre = textoLivre.substring(0, textoLivre.length() - 4);
				}

				linhaRetorno.append(textoLivre).append(' ');
			}
			linhaRetorno.append(_P_).append(campo.getCampoLaudo().getNome()).append(_BARRA_P_);
			for (AelValorNormalidCampo normalidade : normalidades) {

				String descricaoNormalidade = (normalidade.getUnidadeMedida() != null
						&& normalidade.getUnidadeMedida().getDescricao() != null)
								? normalidade.getUnidadeMedida().getDescricao() : "";

				/* Valores minimos */
				Double v_valor_minimo_num = null;
				String v_valor_minimo_txt = null;

				/* Valores máximos */
				Double v_valor_maximo_num = null;
				String v_valor_maximo_txt = null;

				if (CoreUtil.isNumeroInteger(normalidade.getValorMinimo())) {
					if (normalidade.getQtdeCasasDecimais() == null
							|| normalidade.getQtdeCasasDecimais().shortValue() == 0) {
						v_valor_minimo_txt = normalidade.getValorMinimo().toString();
					} else {
						v_valor_minimo_num = (new Double(normalidade.getValorMinimo()) / Math.pow(new Double(10),
								(normalidade.getQtdeCasasDecimais() != null) ? normalidade.getQtdeCasasDecimais() : 0));
						v_valor_minimo_txt = v_valor_minimo_num.toString();
					}

				} else {
					v_valor_minimo_txt = normalidade.getValorMinimo();
				}

				if (CoreUtil.isNumeroInteger(normalidade.getValorMaximo())) {
					if (normalidade.getQtdeCasasDecimais() == null
							|| normalidade.getQtdeCasasDecimais().shortValue() == 0) {
						v_valor_maximo_txt = normalidade.getValorMaximo().toString();
					} else {
						v_valor_maximo_num = (new Double(normalidade.getValorMaximo()) / Math.pow(new Double(10),
								(normalidade.getQtdeCasasDecimais() != null) ? normalidade.getQtdeCasasDecimais() : 0));
						v_valor_maximo_txt = v_valor_maximo_num.toString();
					}
				} else {
					v_valor_maximo_txt = normalidade.getValorMaximo();
				}
				linhaRetorno.append(_P_);
				if (DominioSexo.M.equals(normalidade.getSexo())){
					linhaRetorno.append("Homens: ");
				} else if (DominioSexo.F.equals(normalidade.getSexo())) {
					linhaRetorno.append("Mulheres: ");
				} 
				montaDescricaoReferencia(linhaRetorno, v_valor_minimo_txt, v_valor_maximo_txt,	descricaoNormalidade);
				linhaRetorno.append(_BARRA_P_);
			}
		}
		return StringEscapeUtils.unescapeHtml4(linhaRetorno.toString());
	}

	/**
	 * ORADB AELP_GET_MASC_VALREF
	 * 
	 * @param solicitacaoExameSeq
	 * @param itemSolicitacaoExameSeq
	 * @param campoLaudoSeq
	 * @return
	 * @throws BaseException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String buscaInformacaoValoresReferenciaHist(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq, AelParametroCamposLaudo campo) throws BaseException {
		AelSolicitacaoExamesHist solicitacaoExame = getAelSolicitacaoExameDAO().obterPeloIdHist(solicitacaoExameSeq);
		StringBuffer linhaRetorno = new StringBuffer();

		if(solicitacaoExame != null){
			AelAtendimentoDiversos atendimentoDiverso = solicitacaoExame.getAtendimentoDiverso();
			AghAtendimentos atendimento = solicitacaoExame.getAtendimento();

			if(atendimentoDiverso != null){
				if(atendimentoDiverso.getOrigemAmostra() != null && atendimentoDiverso.getOrigemAmostra().equals("N")){
					return "";
				}else{
					if(atendimentoDiverso.getAipPaciente() == null){
						if(atendimentoDiverso.getSexo() == null || atendimentoDiverso.getDtNascimento() == null){
							return "Valor de referência não informado por falta de dados cadastrais do paciente.";
						}
					}
				}
			}

			AghParametros parametroAreaExec = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
			AghParametros parametroDtNovaMasc = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DATA_IMPL_NOVA_MASCARA);

			AelExtratoItemSolicitacao itemExtrato = getAelExtratoItemSolicitacaoDAO().obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntrada(solicitacaoExameSeq, itemSolicitacaoExameSeq, parametroAreaExec.getVlrTexto());

			if(itemExtrato != null){
				if(CoreUtil.isMenorDatas(itemExtrato.getDataHoraEvento(), parametroDtNovaMasc.getVlrData())){
					return "";
				}

				DominioSexo sexoPaciente = null;
				Date dtNascimento = null;

				if(atendimento != null){
					sexoPaciente = atendimento.getPaciente().getSexo();
					dtNascimento = atendimento.getPaciente().getDtNascimento();
				}else{
					sexoPaciente = atendimentoDiverso.getAipPaciente().getSexo();
					dtNascimento = atendimentoDiverso.getAipPaciente().getDtNascimento();
				}

				Integer idadeAnos = DateUtil.obterQtdAnosEntreDuasDatas(dtNascimento, itemExtrato.getDataHoraEvento());
				Integer idadeMeses = DateUtil.obterQtdMesesEntreDuasDatas(dtNascimento, itemExtrato.getDataHoraEvento());
				Integer idadeDias = DateUtil.obterQtdDiasEntreDuasDatas(dtNascimento, itemExtrato.getDataHoraEvento());

				List<AelValorNormalidCampo> normalidades = getAelValorNormalidCampoDAO().buscaNormalidadeCampoLaudoSemIdade(campo.getId().getCalSeq(), itemExtrato.getDataHoraEvento(), sexoPaciente); 

				if(campo.getCampoLaudoRelacionado() != null && (normalidades == null || normalidades.size() == 0)) {
					normalidades = getAelValorNormalidCampoDAO().buscaNormalidadeCampoLaudoSemIdade(campo.getCampoLaudoRelacionado().getSeq(), itemExtrato.getDataHoraEvento(), sexoPaciente);				
				}

				if(normalidades==null || normalidades.size() == 0){
					return "";
				}else{
					String textoLivre = campo.getTextoLivre();
					
					if (textoLivre.startsWith("<p>")) {
						// Remove o <p> do inicio
						textoLivre = textoLivre.substring(3);
					}
					
					if (textoLivre.endsWith("</p>")) {
						// Remove o </p> do fim
						textoLivre = textoLivre.substring(0, textoLivre.length() - 4);
					}
					
					linhaRetorno.append(textoLivre).append(' ');
				}

				for (AelValorNormalidCampo normalidade : normalidades) {

					String descricaoNormalidade = (normalidade.getUnidadeMedida()!=null && normalidade.getUnidadeMedida().getDescricao()!=null)?normalidade.getUnidadeMedida().getDescricao():"";

					/*Valores minimos*/
					Double v_valor_minimo_num = null;
					String v_valor_minimo_txt = null;

					/*Valores máximos*/
					Double v_valor_maximo_num = null;
					String v_valor_maximo_txt = null;

					if(CoreUtil.isNumeroInteger(normalidade.getValorMinimo())){
						if(normalidade.getQtdeCasasDecimais()==null || normalidade.getQtdeCasasDecimais().shortValue()==0){
							v_valor_minimo_txt = normalidade.getValorMinimo().toString();
						}else{
							v_valor_minimo_num = (new Double(normalidade.getValorMinimo()) / Math.pow(new Double(10), (normalidade.getQtdeCasasDecimais()!=null)?normalidade.getQtdeCasasDecimais():0));
							v_valor_minimo_txt = v_valor_minimo_num.toString();	
						}

					}else{
						v_valor_minimo_txt = normalidade.getValorMinimo();
					}

					if(CoreUtil.isNumeroInteger(normalidade.getValorMaximo())){
						if(normalidade.getQtdeCasasDecimais()==null || normalidade.getQtdeCasasDecimais().shortValue()==0){
							v_valor_maximo_txt = normalidade.getValorMaximo().toString();
						}else{
							v_valor_maximo_num = (new Double(normalidade.getValorMaximo()) / Math.pow(new Double(10), (normalidade.getQtdeCasasDecimais()!=null)?normalidade.getQtdeCasasDecimais():0));
							v_valor_maximo_txt = v_valor_maximo_num.toString();	
						}
					}else{
						v_valor_maximo_txt = normalidade.getValorMaximo();
					}

					if(normalidade.getIdadeMinima() == null && normalidade.getIdadeMaxima() == null){
						montaDescricaoReferencia(linhaRetorno, v_valor_minimo_txt, v_valor_maximo_txt, descricaoNormalidade);

					}else if(normalidade.getUnidMedidaIdade().equals(DominioUnidadeMedidaIdade.D)){
						if(idadeAnos.intValue()==0 && idadeMeses.intValue()==0 && normalidade.getIdadeMinima()!=null && normalidade.getIdadeMaxima()!=null
								&& CoreUtil.isBetweenRange(idadeDias, normalidade.getIdadeMinima().intValue(), normalidade.getIdadeMaxima().intValue())){
							montaDescricaoReferencia(linhaRetorno, v_valor_minimo_txt, v_valor_maximo_txt, descricaoNormalidade);
						}

					}else if(normalidade.getUnidMedidaIdade().equals(DominioUnidadeMedidaIdade.M)){
						if(idadeAnos.intValue()==0 && normalidade.getIdadeMinima()!=null && normalidade.getIdadeMaxima()!=null
								&& CoreUtil.isBetweenRange(idadeMeses, normalidade.getIdadeMinima().intValue(), normalidade.getIdadeMaxima().intValue())){
							montaDescricaoReferencia(linhaRetorno, v_valor_minimo_txt, v_valor_maximo_txt, descricaoNormalidade);
						}

					}else if(normalidade.getUnidMedidaIdade().equals(DominioUnidadeMedidaIdade.A)){
						if(normalidade.getIdadeMinima()!=null && normalidade.getIdadeMaxima()!=null
								&& CoreUtil.isBetweenRange(idadeAnos, normalidade.getIdadeMinima().intValue(), normalidade.getIdadeMaxima().intValue())){
							montaDescricaoReferencia(linhaRetorno, v_valor_minimo_txt, v_valor_maximo_txt, descricaoNormalidade);
						}
					}
				}
			}
		}
		return StringEscapeUtils.unescapeHtml4(linhaRetorno.toString());
	}

	private void montaDescricaoReferencia(StringBuffer linhaRetorno, String v_valor_minimo_txt, String v_valor_maximo_txt, String descricaoNormalidade){
		if(v_valor_minimo_txt == null && v_valor_maximo_txt != null){
			linhaRetorno.append("até ");
			if(v_valor_maximo_txt.indexOf(',')==0){
				linhaRetorno.append('0').append(v_valor_maximo_txt).append(' ').append(descricaoNormalidade);
			}else{
				linhaRetorno.append(v_valor_maximo_txt).append(' ').append(descricaoNormalidade);
			}

		}else if(v_valor_minimo_txt != null && v_valor_maximo_txt == null){
			if(v_valor_minimo_txt.indexOf(',')==0){
				linhaRetorno.append('0').append(v_valor_minimo_txt).append(' ').append(descricaoNormalidade).append(" acima");
			}else{
				linhaRetorno.append(v_valor_minimo_txt).append(' ').append(descricaoNormalidade).append(" acima");
			}
		}else if(v_valor_minimo_txt != null && v_valor_maximo_txt != null){
			if(v_valor_minimo_txt.indexOf(',')==0){
				linhaRetorno.append('0').append(v_valor_minimo_txt);
			}else{
				linhaRetorno.append(v_valor_minimo_txt);
			}

			linhaRetorno.append(" a ");

			if(v_valor_maximo_txt.indexOf(',')==0){
				linhaRetorno.append('0').append(v_valor_maximo_txt);
			}else{
				linhaRetorno.append(v_valor_maximo_txt);
			}

			linhaRetorno.append(' ').append(descricaoNormalidade);
		}	
	}

	protected AelValorNormalidCampoDAO getAelValorNormalidCampoDAO(){
		return aelValorNormalidCampoDAO;
	}

	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO(){
		return aelAmostraItemExamesDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO(){
		return aelItemSolicitacaoExameDAO;
	}

	protected AelItemSolicExameHistDAO getAelItemSolicExameHistDAO(){
		return aelItemSolicExameHistDAO;
	}

	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO(){
		return aelSolicitacaoExameDAO;
	}

	protected AelEquipamentosDAO getAelEquipamentosDAO(){
		return aelEquipamentosDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO(){
		return aelExtratoItemSolicitacaoDAO;
	}
	
	protected AelExtratoItemSolicHistDAO getAelExtratoItemSolicHistDAO(){
		return aelExtratoItemSolicHistDAO;
	}

	protected AelMetodoDAO getAelMetodoDAO(){
		return aelMetodoDAO;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

}