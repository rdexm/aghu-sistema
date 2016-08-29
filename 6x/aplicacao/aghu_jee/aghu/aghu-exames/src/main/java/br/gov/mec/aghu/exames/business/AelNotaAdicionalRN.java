package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFormaIdentificacaoCaixaPostal;
import br.gov.mec.aghu.dominio.DominioSituacaoCxtPostalServidor;
import br.gov.mec.aghu.dominio.DominioTipoMensagemCaixaPostal;
import br.gov.mec.aghu.dominio.DominioTipoMensagemExame;
import br.gov.mec.aghu.dominio.DominioTipoNomeParam;
import br.gov.mec.aghu.exames.dao.AelItemSolicConsultadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelNotaAdicionalDAO;
import br.gov.mec.aghu.model.AelItemSolicConsultado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelNotaAdicional;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.AghCaixaPostalAplicacao;
import br.gov.mec.aghu.model.AghCaixaPostalAplicacaoId;
import br.gov.mec.aghu.model.AghCaixaPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostalServidorId;
import br.gov.mec.aghu.model.AghParametroAplicacao;
import br.gov.mec.aghu.model.AghParametroAplicacaoId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateValidator;

/**
 * 
 * @author amalmeida
 * 
 */
@Stateless
public class AelNotaAdicionalRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelNotaAdicionalRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelNotaAdicionalDAO aelNotaAdicionalDAO;
	
	@Inject
	private AelItemSolicConsultadoDAO aelItemSolicConsultadoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5960016652904441045L;

	/**
	 * ORADB AELT_NTC_BRI (INSERT)
	 * @param notaAdicional
	 * @throws BaseException
	 */
	private void preInserir(AelNotaAdicional notaAdicional) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		notaAdicional.setCriadoEm(new Date());//RN1
		notaAdicional.setServidor(servidorLogado);//RN2
	}

	/**
	 * Inserir AelNotaAdicional
	 * @param notaAdicional
	 * @throws BaseException
	 */
	public void inserir(AelNotaAdicional notaAdicional) throws BaseException{
		this.preInserir(notaAdicional);		
		this.getAelNotaAdicionalDAO().persistir(notaAdicional);
		this.posInserir(notaAdicional);
		this.getAelNotaAdicionalDAO().flush();
	}

	/**
	 * ORADB AELT_NTC_ARI (INSERT) 
	 * @param notaAdicional
	 * @throws BaseException
	 */
	private void posInserir(AelNotaAdicional notaAdicional) throws BaseException{

		this.verificarSolicitacaoExameConsultada(notaAdicional);//atualiza AelItemSolicitacaoExames


	}

	/**
	 * ORADB PROCEDURE aelk_ntc_rn.rn_ntcp_atu_cx_post
	 *   Esta regra verifica se uma determinada solicitacao de exames
	 *   	ja foi consultada por algum servidor.Para isso, utiliza a tabela
	 *   	<ael_item_solic_consultados>.Caso algum servidor já tenha consultado,
	 *   	eh feita a inclusão na <agh_caixa_postais> e tabelas vizinhas,
	 *   	isso porque a nota adicional referente a solicitacao em questão,
	 *   	está alterando um resultado de exames já consultado.
	 *   	 
	 * @param notaAdicional
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void verificarSolicitacaoExameConsultada(AelNotaAdicional notaAdicional)throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		Integer iseSoeSeq = notaAdicional.getId().getIseSoeSeq(); //p_new_soe_seq
		Short iseSeqp = notaAdicional.getId().getIseSeqp().shortValue(); // p_new_seqp

		Long vCxtSeq = null;
		String vExameMaterial = null;
		String vMensagem  = null;
		String vUser = servidorLogado.getUsuario();
		String vTipoRel = null;
		String vNomePaciente = null;

		List<AelItemSolicConsultado> listAelItemSolicConsultaos = this.getAelItemSolicConsultadoDAO().pesquisarAelItemSolicConsultadosResultadosExames(iseSoeSeq, iseSeqp);

		if(listAelItemSolicConsultaos !=null && !listAelItemSolicConsultaos.isEmpty()) {

			if(notaAdicional.getItemSolicitacaoExame()!=null && notaAdicional.getItemSolicitacaoExame().getExame()!=null){
				vExameMaterial = notaAdicional.getItemSolicitacaoExame().getExame().getDescricaoUsual();
			}

			AelSolicitacaoExames solicitacaoExames = notaAdicional.getItemSolicitacaoExame().getSolicitacaoExame();
			vNomePaciente =  buscarLaudoNomePaciente(solicitacaoExames);

			//ATENÇÃO: passar para properties!!!!
			vMensagem = vExameMaterial + " de "+ vNomePaciente + " com laudo modificado."; 

			AghCaixaPostal caixaPostal = new AghCaixaPostal();
			// caixaPostal.setSeq(v_cxt_seq);  //v_cxt_seq := aghc_get_agh_cxt_sq1_nextval;
			caixaPostal.setDthrInicio(new Date());
			caixaPostal.setTipoMensagem(DominioTipoMensagemExame.A);
			caixaPostal.setAcaoObrigatoria(Boolean.TRUE);
			caixaPostal.setMensagem(vMensagem);
			caixaPostal.setCriadoEm(new Date());
			caixaPostal.setFormaIdentificacao(DominioFormaIdentificacaoCaixaPostal.R);
			caixaPostal.setNomeRotina("AELP_INCLUI_SERVIDOR");

			/**
			 *  Insere em agh_caixa_postais 
			 *  
			 */
			this.getAghuFacade().persistirAghCaixaPostal(caixaPostal);
			this.flush();
			vCxtSeq = caixaPostal.getSeq();

			AghCaixaPostalAplicacaoId caixaPostalAplicacaoID = new AghCaixaPostalAplicacaoId();
			caixaPostalAplicacaoID.setAplicacaoCodigo("AELP_GRAVA_RESULTADO");//TODO verificar essa ROTINA: AELP_GRAVA_RESULTADO!!
			caixaPostalAplicacaoID.setCxtSeq(vCxtSeq);

			AghCaixaPostalAplicacao caixaPostalAplicacao = new AghCaixaPostalAplicacao();
			caixaPostalAplicacao.setId(caixaPostalAplicacaoID);
			caixaPostalAplicacao.setOrdemChamada(Short.valueOf("1"));
			caixaPostalAplicacao.setAghCaixaPostal(caixaPostal);

			/**
			 *  Insere em agh_caixa_postal_aplicacoes 
			 */
			this.getAghuFacade().inserirAghCaixaPostalAplicacao(caixaPostalAplicacao, Boolean.TRUE);

			/**
			 *  insere o primeiro parametro soe_seq para <AELP_GRAVA_RESULTADO>
			 *  
			 */
			AghParametroAplicacaoId paramAplicId = new AghParametroAplicacaoId();
			paramAplicId.setCxaAplicacaoCodigo(caixaPostalAplicacao.getId().getAplicacaoCodigo());
			paramAplicId.setCxaCxtSeq(caixaPostalAplicacao.getId().getCxtSeq());

			AghParametroAplicacao parametroAplicacao = new AghParametroAplicacao();
			parametroAplicacao.setId(paramAplicId);
			parametroAplicacao.setAghCaixaPostalAplicacao(caixaPostalAplicacao);
			parametroAplicacao.setParametros(iseSoeSeq.toString());
			parametroAplicacao.setOrdem(Short.valueOf("1"));
			parametroAplicacao.setTipoNomeParam(DominioTipoNomeParam.P); //TODO RETIRARRRRRRR E ALTERAR POJOO PARA NULL!!!!!!!!!!!!!!
			this.getAghuFacade().inserirAghParametroAplicacao(parametroAplicacao, Boolean.TRUE);

			/**
			 *  Insere em agh_parametro_aplicacoes o segundo parametro ise_seqp para <AELP_GRAVA_RESULTADO> 
			 *  
			 */
			parametroAplicacao = new AghParametroAplicacao();
			parametroAplicacao.setId(paramAplicId);
			parametroAplicacao.setAghCaixaPostalAplicacao(caixaPostalAplicacao);
			parametroAplicacao.setParametros(iseSeqp.toString());
			parametroAplicacao.setOrdem(Short.valueOf("2"));
			parametroAplicacao.setTipoNomeParam(DominioTipoNomeParam.P);

			this.getAghuFacade().inserirAghParametroAplicacao(parametroAplicacao, Boolean.TRUE);


			/**
			 *  <IMPLAUDO> CHAMADA DO DELPHI
			 */
			caixaPostalAplicacaoID = new AghCaixaPostalAplicacaoId();
			caixaPostalAplicacaoID.setAplicacaoCodigo("IMPLAUDO");
			caixaPostalAplicacaoID.setCxtSeq(vCxtSeq);

			caixaPostalAplicacao = new AghCaixaPostalAplicacao();
			caixaPostalAplicacao.setId(caixaPostalAplicacaoID);
			caixaPostalAplicacao.setOrdemChamada(Short.valueOf("2")); 
			caixaPostalAplicacao.setAghCaixaPostal(caixaPostal);

			this.getAghuFacade().inserirAghCaixaPostalAplicacao(caixaPostalAplicacao, Boolean.TRUE);

			/** USER
			 *  Insere em agh_parametro_aplicacoes (primeiro parametro user) 
			 */
			paramAplicId = new AghParametroAplicacaoId();
			paramAplicId.setCxaAplicacaoCodigo(caixaPostalAplicacao.getId().getAplicacaoCodigo());
			paramAplicId.setCxaCxtSeq(caixaPostalAplicacao.getId().getCxtSeq());

			parametroAplicacao = new AghParametroAplicacao();
			parametroAplicacao.setId(paramAplicId);
			parametroAplicacao.setAghCaixaPostalAplicacao(caixaPostalAplicacao);
			parametroAplicacao.setOrdem(Short.valueOf("1"));
			parametroAplicacao.setTipoNomeParam(DominioTipoNomeParam.P);


			parametroAplicacao.setParametros(vUser); //vUser

			this.getAghuFacade().inserirAghParametroAplicacao(parametroAplicacao, Boolean.TRUE);


			/**
			 *  Segundo parametro PASSWORD
			 *  Obs.: este parametro nao é armazenado. Ele e setado no proprio forms caixa postal 
			 */

			parametroAplicacao = new AghParametroAplicacao();
			parametroAplicacao.setId(paramAplicId);
			parametroAplicacao.setAghCaixaPostalAplicacao(caixaPostalAplicacao);
			parametroAplicacao.setOrdem(Short.valueOf("2"));
			parametroAplicacao.setParametros("Senha");
			parametroAplicacao.setTipoNomeParam(DominioTipoNomeParam.P);

			this.getAghuFacade().inserirAghParametroAplicacao(parametroAplicacao, Boolean.TRUE);


			/** SESSAO
			 * Segundo parametro v_seq  para a tabela AEL_ITENS_RESUL_IMPRESSAO
			 * Como este parâmetro é referente a SESSAO ele tambem é setado no forms caixa postal
			 */
			parametroAplicacao = new AghParametroAplicacao();
			parametroAplicacao.setId(paramAplicId);
			parametroAplicacao.setAghCaixaPostalAplicacao(caixaPostalAplicacao);
			parametroAplicacao.setOrdem(Short.valueOf("3"));
			parametroAplicacao.setParametros("SESSAO");
			parametroAplicacao.setTipoNomeParam(DominioTipoNomeParam.P);

			this.getAghuFacade().inserirAghParametroAplicacao(parametroAplicacao, Boolean.TRUE);

			/**
			 * 4º parametro tipo rel
			 */
			vTipoRel = "1";

			parametroAplicacao = new AghParametroAplicacao();
			parametroAplicacao.setId(paramAplicId);
			parametroAplicacao.setAghCaixaPostalAplicacao(caixaPostalAplicacao);
			parametroAplicacao.setOrdem(Short.valueOf("4"));
			parametroAplicacao.setParametros(vTipoRel);
			parametroAplicacao.setTipoNomeParam(DominioTipoNomeParam.P);

			this.getAghuFacade().inserirAghParametroAplicacao(parametroAplicacao, Boolean.TRUE);

			/**
			 * Parametro Aplicacao DataBase "v_database_name".
			 */

			parametroAplicacao = new AghParametroAplicacao();
			parametroAplicacao.setId(paramAplicId);
			parametroAplicacao.setAghCaixaPostalAplicacao(caixaPostalAplicacao);
			parametroAplicacao.setOrdem(Short.valueOf("5"));
			parametroAplicacao.setTipoNomeParam(DominioTipoNomeParam.P);

			parametroAplicacao.setParametros("TEste Postgress");

			this.getAghuFacade().inserirAghParametroAplicacao(parametroAplicacao, Boolean.TRUE);

			/**
			 * UPDATE  ael_item_solicitacao_exames
			 */
			AelItemSolicitacaoExames itemSolicitacaoExame = notaAdicional.getItemSolicitacaoExame();
			itemSolicitacaoExame.setIndTipoMsgCxPostal(DominioTipoMensagemCaixaPostal.NA);
			itemSolicitacaoExame.setDthrMsgCxPostal(new Date());
			this.getAelItemSolicitacaoExameDAO().atualizar(itemSolicitacaoExame);
			this.getAelItemSolicitacaoExameDAO().flush();

			/* FIM da procedure aelk_ntc_rn.rn_ntcp_atu_cx_post */



			/** PROCEDURE AELP_INCLUI_SERVIDOR
			 *  Chama a Rotina AELP_INCLUI_SERVIDOR 
			 *  Não tem na Procedure migrada, mas era executada no Oracle através do parametro acima setado: ->> caixaPostal.setNomeRotina("AELP_INCLUI_SERVIDOR");
			 */
			List<Object[]> servidoresConsultaos = this.getAelItemSolicConsultadoDAO().pesquisarServidorAelItemSolicConsultadosResultadosExames(iseSoeSeq, iseSeqp);

			Iterator<Object[]> it = servidoresConsultaos.iterator();
			while (it.hasNext()) {

				Object[] obj = it.next();
				RapServidoresId rapServidoresId = new RapServidoresId();
				rapServidoresId.setMatricula((Integer) obj[0]);
				rapServidoresId.setVinCodigo((Short) obj[1]);
				RapServidores servidor = this.getRegistroColaboradorFacade().obterServidor(rapServidoresId);

				this.atualizarServidor(iseSoeSeq, iseSeqp, vCxtSeq, servidor);

			}

		}

	}



	/**
	 * ORADB PROCEDURE AELP_INCLUI_SERVIDOR
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param vCxtSeq
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void atualizarServidor(Integer iseSoeSeq, Short iseSeqp,
			Long vCxtSeq, RapServidores servidor) throws ApplicationBusinessException {

		String  vAplicacaoCodigo = null;
		String vIseSoeSeq = null;
		String vIseSeqp = null;
		Date vDataNota = null;
		Date vDataCons = null;

		AghCaixaPostalServidor aghCaixaPostalServidor = null;

		AghParametros parametroColeta = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_APLICACAO_LAUDO);

		vAplicacaoCodigo = parametroColeta.getVlrTexto();

		AghCaixaPostalAplicacaoId aghCaixaPostalAplicacaoID = new AghCaixaPostalAplicacaoId();
		aghCaixaPostalAplicacaoID.setAplicacaoCodigo(vAplicacaoCodigo);
		aghCaixaPostalAplicacaoID.setCxtSeq(vCxtSeq);

		List<AghParametroAplicacao> listaParametrosAplicacao = this.getAghuFacade().pesquisarParametroAplicacaoPorCaixaPostalAplicacao(aghCaixaPostalAplicacaoID);

		for(AghParametroAplicacao parametroAplic: listaParametrosAplicacao){

			if(parametroAplic.getOrdem().equals(Short.valueOf("1"))){

				vIseSoeSeq = parametroAplic.getParametros();

			}else if(parametroAplic.getOrdem().equals(Short.valueOf("2"))){

				vIseSeqp =  parametroAplic.getParametros();
			}
		}

		if(vIseSoeSeq !=null && vIseSeqp!=null){
			vDataNota = this.getAelNotaAdicionalDAO().obterMaxCriadoEm(Integer.valueOf(vIseSoeSeq), Short.valueOf(vIseSeqp));
		}

		vDataCons = this.getAelItemSolicConsultadoDAO().obterMaxCriadoEm(iseSoeSeq, iseSeqp, servidor);

		if(vDataCons != null && vDataNota != null){

			if(DateValidator.validaDataTruncadaMaiorIgual(vDataNota, vDataCons)){

				//TODO verificar se ja existe, se se sim faz update!!!
				AghCaixaPostalServidorId aghCaixaPostalServidorId = new AghCaixaPostalServidorId();
				aghCaixaPostalServidorId.setCxtSeq(vCxtSeq);
				aghCaixaPostalServidorId.setServidor(servidor);


				aghCaixaPostalServidor = getAghuFacade().obterAghCaixaPostalServidor(aghCaixaPostalServidorId);

				if(aghCaixaPostalServidor!=null){
					/**
					 * Atualiza agh_caixa_postal_servidores
					 */
					if(!aghCaixaPostalServidor.getSituacao().equals(DominioSituacaoCxtPostalServidor.L)){
						aghCaixaPostalServidor.setSituacao(DominioSituacaoCxtPostalServidor.N); 
						getAghuFacade().atualizarAghCaixaPostalServidor(aghCaixaPostalServidor);
					}
				}else{
					/**
					 * Insere em agh_caixa_postal_servidores
					 */
					aghCaixaPostalServidor = new AghCaixaPostalServidor();
					aghCaixaPostalServidor.setId(aghCaixaPostalServidorId);
					aghCaixaPostalServidor.setSituacao(DominioSituacaoCxtPostalServidor.N); 
					getAghuFacade().persistirAghCaixaPostalServidor(aghCaixaPostalServidor);
				}
			}
		}
	}


	/**
	 * ORADB AELC_LAUDO_NOME_PAC
	 * 
	 * @param solicitacaoExames
	 * @return
	 */
	public String buscarLaudoNomePaciente(AelSolicitacaoExames solicitacaoExames) {
		String vNome = "";
		if(solicitacaoExames != null){
			if(solicitacaoExames.getAtendimento() != null){
				if(solicitacaoExames.getAtendimento().getPaciente() != null && solicitacaoExames.getAtendimento().getPaciente().getNome() != null){
					return solicitacaoExames.getAtendimento().getPaciente().getNome();
				}
			}else{
				if(solicitacaoExames.getAtendimentoDiverso() != null){
					if(solicitacaoExames.getAtendimentoDiverso().getAipPaciente() != null){
						String vNomePac = solicitacaoExames.getAtendimentoDiverso().getAipPaciente().getNome();

						if(solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas() != null){
							if(solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero() != null){
								String vPjqNumero = "GPP-"+solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero();
								vNome = vPjqNumero+" - "+vNomePac;
							}else{
								vNome = vNomePac;
							}
						}
					}else if(solicitacaoExames.getAtendimentoDiverso().getNomePaciente() != null){
						String vNomePac = solicitacaoExames.getAtendimentoDiverso().getNomePaciente();

						if(solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas() != null){
							if(solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero() != null){
								String vPjqNumero = "GPP-"+solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero();
								vNome = vPjqNumero+" - "+vNomePac;
							}else{
								vNome = vNomePac;
							}
						}
					}
					else if(solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas() != null){
						vNome =  "GPP-"+solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero()+" - "+solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNome();
					}else if(solicitacaoExames.getAtendimentoDiverso().getAelCadCtrlQualidades() != null){
						vNome =  solicitacaoExames.getAtendimentoDiverso().getAelCadCtrlQualidades().getMaterial();
					}else if(solicitacaoExames.getAtendimentoDiverso().getAelLaboratorioExternos() != null){
						vNome = solicitacaoExames.getAtendimentoDiverso().getAelLaboratorioExternos().getNome();
					}else if(solicitacaoExames.getAtendimentoDiverso().getAelDadosCadaveres() != null){
						vNome = solicitacaoExames.getAtendimentoDiverso().getAelDadosCadaveres().getNome();
					}else if(solicitacaoExames.getAtendimentoDiverso().getAbsCandidatosDoadores() != null){
						vNome = solicitacaoExames.getAtendimentoDiverso().getAbsCandidatosDoadores().getNome();
					}
				}
			}
		}
		if(!vNome.equals("")) {
			vNome = vNome.substring(1,vNome.length() <= 50 ? vNome.length() : 50);
		}

		return vNome;
	}


	protected AelNotaAdicionalDAO getAelNotaAdicionalDAO() {
		return aelNotaAdicionalDAO;
	}

	protected AelItemSolicConsultadoDAO getAelItemSolicConsultadoDAO() {
		return aelItemSolicConsultadoDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}
