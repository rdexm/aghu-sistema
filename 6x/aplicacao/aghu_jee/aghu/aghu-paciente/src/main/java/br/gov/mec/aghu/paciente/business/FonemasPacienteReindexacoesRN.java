package br.gov.mec.aghu.paciente.business;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.temp.AipFonemaPacientesRotinaFonema;
import br.gov.mec.aghu.model.temp.AipFonemasMaePacienteRotinaFonema;
import br.gov.mec.aghu.model.temp.AipPacientesRotinaFonema;
import br.gov.mec.aghu.model.temp.AipPosicaoFonemasMaePacienteRotinaFonema;
import br.gov.mec.aghu.model.temp.AipPosicaoFonemasMaePacienteRotinaFonemaId;
import br.gov.mec.aghu.model.temp.AipPosicaoFonemasPacientesRotinaFonema;
import br.gov.mec.aghu.model.temp.AipPosicaoFonemasPacientesRotinaFonemaId;
import br.gov.mec.aghu.paciente.dao.AipFonemasMaePacienteDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesRotinaFonemaDAO;
import br.gov.mec.aghu.paciente.dao.AipPosicaoFonemasMaePacienteRotinaFonemaDAO;
import br.gov.mec.aghu.paciente.dao.AipPosicaoFonemasPacientesRotinaFonemaDAO;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.business.fonetizador.FonetizadorUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * Classe de apoio para as Business Facades. Ela em geral agrupa as
 * funcionalidades encontradas em packages e procedures do AGHU.
 * 
 * Ela poderia ser uma classe com acesso friendly ou default e não ser um
 * componente seam.
 * 
 * Mas fazendo assim facilita, pois ela também pode receber uma referência para
 * o EntityManager.
 * 
 * Outra forma de fazer é instanciar ela diretamente do ON e passar o entity
 * manager para seus parâmetros. Neste caso os metodos desta classe poderiam ser
 * até estaticos e nao necessitar de instanciação. Ai ela seria apenas um
 * particionamento lógico de código e não um componente que possa ser injetado
 * em qualquer outro contexto.
 * 
 * ATENÇÃO, Os metodos desta classe nunca devem ser acessados diretamente por
 * qualquer classe que não a ON correspondente. Por isso sugere-se que todos os
 * métodos desta sejam friendly (default) ou private.
 * 
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FonemasPacienteReindexacoesRN extends BaseBMTBusiness {

		
	private static final String END_NL = "); \n";

	private static final Log LOG = LogFactory.getLog(FonemasPacienteReindexacoesRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AipPosicaoFonemasPacientesRotinaFonemaDAO aipPosicaoFonemasPacientesRotinaFonemaDAO;
	
	@Inject
	private AipPacientesRotinaFonemaDAO aipPacientesRotinaFonemaDAO;
	
	@Inject
	private AipFonemasMaePacienteDAO aipFonemasMaePacienteDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private AipPosicaoFonemasMaePacienteRotinaFonemaDAO aipPosicaoFonemasMaePacienteRotinaFonemaDAO;

	private static final int TIMEOUT = 60 * 60 * 12; // 12 horas
	
	private static final long serialVersionUID = -670560734535450722L;


	/**
	 * Retorna os fonemas associados ao nome e na ordem em que foram digitados.
	 * 
	 * @param nome nome a ser fonetizado.
	 * @return fonemas relacionados ao nome informado.
	 */
	public List<String> obterFonemasNaOrdem(String nome) throws ApplicationBusinessException {
		if (StringUtils.isBlank(nome)) {
			return new ArrayList<String>(0);
		}

		String pFonema = FonetizadorUtil.obterFonema(nome);
		List<String> fonemas = particionarFonemas(pFonema);

		return fonemas;
	}

	/**
	 * Cada fonema tem um tamanho de no máximo 6 caracteres, portanto o tamanho
	 * da string passada como parâmetro sempre vai ser múltiplo de 6.
	 * 
	 * @param pFonema
	 * @return
	 */
	private List<String> particionarFonemas(String pFonema) {
		if (StringUtils.isBlank(pFonema)) {
			return new ArrayList<String>(0);
		}

		List<String> fonemas = new ArrayList<String>();

		int o = (pFonema.length() / 6);
		for (int i = 1; i <= o; i++) {
			fonemas.add(pFonema.substring((6 * (i - 1)), (6 * (i - 1)) + 6));
		}

		return fonemas;
	}

	
	/**
	 * Algoritmo responsável por popular a coluna POSICAO das tabelas
	 * AIP_FONEMA_PACIENTE e AIP_FONEMA_MAE_PACIENTE.
	 * 
	 * Atividade cadastrada em http://redmine.mec.gov.br/issues/1347
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void reindexarPosicaoFonemas() throws ApplicationBusinessException {
		// ALGORITMO 01 - Um algorítmo para fazer o insert no campo
		// POSICAO a partir da base de dados atual. O algoritmo populará
		// todos os registros existentes na base de dados de uma única
		// vez. Para esse algorítmos deverãos ser consideradas as
		// tabelas AIP_FONEMA_PACIENTE, AIP_FONEMA_MAE_PACIENTE e o
		// sinônimo AIP_PACIENTES. A tabela referente ao sinônimo
		// AIP_PACIENTES contém os nomes dos pacientes não
		// criptografados.
		
		Date t1 = new Date();

		try {
			this.beginTransaction(1*60*60*1000);	// 1 hora
			Integer count = 0;
			Integer firstResult = 0;
			Integer maxResults = 1000;

			List<AipPacientesRotinaFonema> listaPacientes = null;

			while (true) {
				pacienteFacade.setTimeout(TIMEOUT); // 12 horas

				listaPacientes = aipPacientesRotinaFonemaDAO.listaPacientesRotinaFonema(firstResult,
					maxResults);

				if (listaPacientes != null && !listaPacientes.isEmpty()) {
					try {
						for (AipPacientesRotinaFonema paciente : listaPacientes) {
							LOG.info("Paciente selecionado: ["
									+ paciente.getCodigo() + "] "
									+ paciente.getNome());

							Set<AipFonemaPacientesRotinaFonema> fonemas = paciente.getFonemas();
							Set<AipFonemasMaePacienteRotinaFonema> fonemasMae = paciente.getFonemasMae();

							List<String> fonemasPacienteNaOrdem = obterFonemasNaOrdem(paciente.getNome());

							List<String> fonemasMaeNaOrdem = obterFonemasNaOrdem(paciente.getNomeMae());

							// Remove as entradas em AipPosicaoFonemasPacientes
							// deste paciente
							aipPosicaoFonemasPacientesRotinaFonemaDAO.removerPosicoesFonemasPacientesRotinaFonema(paciente.getCodigo());
							
							// Remove as entradas em AipPosicaoFonemasPacientes
							// deste paciente
							aipPosicaoFonemasMaePacienteRotinaFonemaDAO.removerPosicaoFonemasMaePacienteRotinaFonema(paciente.getCodigo());
							
							Set<AipPosicaoFonemasPacientesRotinaFonema> posicoesFonemas = new HashSet<AipPosicaoFonemasPacientesRotinaFonema>();
							for (AipFonemaPacientesRotinaFonema fonema : fonemas) {
								String strFonema = fonema.getAipFonemas().getFonema();

								for (int j = 0; j < fonemasPacienteNaOrdem.size(); j++) {
									if (fonemasPacienteNaOrdem.get(j).equals(strFonema)) {
										Byte posicao = (byte) (j + 1);
										posicoesFonemas
												.add(new AipPosicaoFonemasPacientesRotinaFonema(
														new AipPosicaoFonemasPacientesRotinaFonemaId(fonema.getSeq(),posicao),fonema));
									}
								}
							}

							for (AipPosicaoFonemasPacientesRotinaFonema posicaoFonema : posicoesFonemas) {
								aipPosicaoFonemasPacientesRotinaFonemaDAO.persistir(posicaoFonema);
							}

							// Atualiza os fonemas da mãe do paciente
							Set<AipPosicaoFonemasMaePacienteRotinaFonema> posicoesFonemasMae = new HashSet<AipPosicaoFonemasMaePacienteRotinaFonema>();
							for (AipFonemasMaePacienteRotinaFonema fonemaMae : fonemasMae) {
								String strFonema = fonemaMae.getAipFonemas()
										.getFonema();

								for (int j = 0; j < fonemasMaeNaOrdem.size(); j++) {
									if (fonemasMaeNaOrdem.get(j).equals(
											strFonema)) {
										Byte posicao = (byte) (j + 1);
										posicoesFonemasMae
												.add(new AipPosicaoFonemasMaePacienteRotinaFonema(
														new AipPosicaoFonemasMaePacienteRotinaFonemaId(
																fonemaMae.getSeq(),posicao),fonemaMae));
									}
								}
							}

							for (AipPosicaoFonemasMaePacienteRotinaFonema posicaoFonema : posicoesFonemasMae) {
								aipPosicaoFonemasMaePacienteRotinaFonemaDAO.persistir(posicaoFonema);
							}

							count++;
						}
						
						listaPacientes = null;
						
						LOG.info("Comitando...");
						pacienteFacade.commit();
						pacienteFacade.sessionClear();
					} catch (Exception e) {
						LOG.error("Erro ao indexar a posição dos fonemas dos pacientes.", e);
					//	pacienteFacade.sessionClose();
						// TODOMIGRACAO
					}

					firstResult += maxResults;
				} else {
					Integer next = aipPacientesRotinaFonemaDAO.buscaProximoCodigoPacientesRotinaFonema(firstResult);

					if (next != null && next != 0) {
						firstResult = next;
					} else {
						break;
					}
				}
			}

			LOG.info("Quantidade de itens processados: " + count);

			Date t2 = new Date();
			LOG.info("Tempo total: " + ((t2.getTime() - t1.getTime()) / 1000) + " segundos.");
			this.commitTransaction();
			
		} catch (Exception e) {
			this.rollbackTransaction();
			LOG.error("Ocorreu um erro na configuração da transação.", e);
			throw new ApplicationBusinessException(ReindexarPacientesONExceptionCode.MENSAGEM_ERRO_REINDEXAR_FONEMAS_PACIENTES);
		}
	}

	public enum ReindexarPacientesONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_REINDEXAR_FONEMAS_PACIENTES
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public File refonetizarPacientes(Boolean somentePacientesSemFonemas) {
		
		this.beginTransaction(TIMEOUT); // 12 horas
		

		IPacienteFacade pacienteFacade = this.getPacienteFacade();
		AipPacientesRotinaFonemaDAO aipPacientesRotinaFonemaDAO = this.getAipPacientesRotinaFonemaDAO();
		
		File zipFile = null;
		File fonemaFile = null;
		File fonemaPacFile = null;
		File fonemaMaePacFile = null;
		File fonemaSocialPacFile = null;
		
		InputStream fonemaFileIS = null;
		InputStream fonemaPacFileIS = null;
		InputStream fonemaMaePacFileIS = null;
		InputStream fonemaSocialPacFileIS = null;
		
		OutputStream fonemaFileOS = null;
		OutputStream fonemaPacFileOS = null;
		OutputStream fonemaMaePacFileOS = null;
		OutputStream fonemaSocialPacFileOS = null;
		
		ZipOutputStream zipFileOS = null;
		
		try {
			fonemaFile = criaArquivoTemporario();
			fonemaPacFile = criaArquivoTemporario();
			fonemaMaePacFile = criaArquivoTemporario();
			fonemaSocialPacFile = criaArquivoTemporario();
			
			fonemaFileOS = new FileOutputStream(fonemaFile, true);
			fonemaPacFileOS = new FileOutputStream(fonemaPacFile, true);
			fonemaMaePacFileOS = new FileOutputStream(fonemaMaePacFile, true);
			fonemaSocialPacFileOS = new FileOutputStream(fonemaSocialPacFile, true);
			
			if(!somentePacientesSemFonemas){
				fonemaFileOS.write("DELETE FROM AGH.AIP_POSI_FONEMAS_PACIENTES; \n".getBytes());
				fonemaFileOS.write("DELETE FROM AGH.AIP_FONEMA_PACIENTES; \n".getBytes());
				fonemaFileOS.write("DELETE FROM AGH.AIP_POSI_FONEMAS_NOME_SOCIAL_PACIENTES; \n".getBytes());
				fonemaFileOS.write("DELETE FROM AGH.AIP_FONEMA_NOME_SOCIAL_PACIENTES; \n".getBytes());
				fonemaFileOS.write("DELETE FROM AGH.AIP_POSI_FONEMAS_MAE_PACIENTES; \n".getBytes());
				fonemaFileOS.write("DELETE FROM AGH.AIP_FONEMAS_MAE_PACIENTE; \n".getBytes());
				fonemaFileOS.write("DELETE FROM AGH.AIP_FONEMAS; \n ".getBytes());
				
				fonemaPacFileOS.write("DELETE FROM AGH.AIP_POSI_FONEMAS_PACIENTES; \n".getBytes());
				fonemaPacFileOS.write("DELETE FROM AGH.AIP_FONEMA_PACIENTES; \n".getBytes());
				
				fonemaSocialPacFileOS.write("DELETE FROM AGH.AIP_POSI_FONEMAS_NOME_SOCIAL_PACIENTES; \n".getBytes());
				fonemaSocialPacFileOS.write("DELETE FROM AGH.AIP_FONEMA_NOME_SOCIAL_PACIENTES; \n".getBytes());
				
				fonemaMaePacFileOS.write("DELETE FROM AGH.AIP_POSI_FONEMAS_MAE_PACIENTES; \n".getBytes());
				fonemaMaePacFileOS.write("DELETE FROM AGH.AIP_FONEMAS_MAE_PACIENTE; \n".getBytes());
			}
			
			Date t1 = new Date();
			
			int count = 1;

			Map<String, Fonema> fonemas = new HashMap<String, Fonema>();

			Integer firstResult = 0;
			Integer maxResults = 5000;

			Long totalPacientes = aipPacientesRotinaFonemaDAO.countPacientesRotinaFonema(somentePacientesSemFonemas);

			List<AipPacientesRotinaFonema> listaPacientes = null;
			Integer seqNomeFonema = 1;
			Integer seqFonemaNomeMae = 1;
			Integer seqFonemaNomeSocial = 1;
			
			while (true) {
				
				listaPacientes = aipPacientesRotinaFonemaDAO.listaPacientesRotinaFonema(firstResult, maxResults, somentePacientesSemFonemas);

				if (listaPacientes != null && !listaPacientes.isEmpty()) {
					try {
						for (AipPacientesRotinaFonema paciente : listaPacientes) {
							seqNomeFonema = fonetizaNomePacientes(paciente, fonemas, seqNomeFonema, fonemaPacFileOS);
							seqFonemaNomeMae = fonetizaNomeMaePaciente(paciente, fonemas, seqFonemaNomeMae, fonemaMaePacFileOS);
							seqFonemaNomeSocial = fonetizaNomeSocialPaciente(paciente, fonemas, seqFonemaNomeSocial, fonemaSocialPacFileOS);

							if (count % 10000 == 0) {
								LOG.info("Total de pacientes processados: " + count + "/" + totalPacientes);
							}
							count++;
						}
						
						fonemaSocialPacFileOS.write("SELECT SETVAL(('AGH.AIP_NOM_SOC_FNP_SQ1'), (SELECT MAX(seq) FROM AGH.AIP_FONEMA_NOME_SOCIAL_PACIENTES));".getBytes());
						fonemaSocialPacFileOS.write("\n".getBytes());
						fonemaMaePacFileOS.write("SELECT SETVAL(('AGH.AIP_FMP_SQ1'), (SELECT MAX(seq) FROM AGH.AIP_FONEMAS_MAE_PACIENTE ));".getBytes());
						fonemaMaePacFileOS.write("\n".getBytes());
						fonemaPacFileOS.write("SELECT SETVAL(('AGH.AIP_FNP_SQ1'), (SELECT MAX(seq) FROM AGH.AIP_FONEMA_PACIENTES ));".getBytes());
						fonemaPacFileOS.write("\n".getBytes());
							
						listaPacientes = null;
						pacienteFacade.sessionClear();
					} catch (Exception e) {
						LOG.error("Erro ao indexar a posição dos fonemas dos pacientes.", e);
						//pacienteFacade.sessionClose();
						//TODOMIGRACAO
					}

					firstResult += maxResults;
				} else {
					Integer next = aipPacientesRotinaFonemaDAO.buscaProximoCodigoPacientesRotinaFonema(firstResult);

					if (next != null && next != 0) {
						firstResult = next;
					} else {
						break;
					}
				}
			}
			
			Iterator<Entry<String, Fonema>> it = fonemas.entrySet().iterator();
			while(it.hasNext()) {
				Entry<String, Fonema> entry = it.next();
				Fonema fonema = entry.getValue();
				
				StringBuilder sqlInsert = new StringBuilder("INSERT INTO AGH.AIP_FONEMAS(FONEMA, CONTADOR, CONTADOR_MAE, CONTADOR_NOME_SOCIAL) VALUES('")
				.append(fonema.getFonema())
				.append("',")
				.append(fonema.getContador())
				.append(",")
				.append(fonema.getContadorMae())
				.append(",")
				.append(fonema.getContadorNomeSocial())
				.append(END_NL);
			fonemaFileOS.write(sqlInsert.toString().getBytes());
			}

			Date t2 = new Date();
			LOG.info("Tempo total: "+ ((t2.getTime() - t1.getTime()) / 1000) + " segundos.");
			
			LOG.info("Compactando arquivos.");
			
			zipFile = criaArquivoTemporario();
			zipFileOS = new ZipOutputStream(new FileOutputStream(zipFile));  
			
            byte[] dados = new byte[TAMANHO_BUFFER];  
            
            
            fonemaFileIS = new BufferedInputStream(new FileInputStream(fonemaFile), TAMANHO_BUFFER);
            fonemaPacFileIS = new BufferedInputStream(new FileInputStream(fonemaPacFile), TAMANHO_BUFFER);
            fonemaMaePacFileIS = new BufferedInputStream(new FileInputStream(fonemaMaePacFile), TAMANHO_BUFFER);
            fonemaSocialPacFileIS = new BufferedInputStream(new FileInputStream(fonemaSocialPacFile), TAMANHO_BUFFER);
            ZipEntry entry = new ZipEntry("fonemas.sql");   
            zipFileOS.putNextEntry(entry);  
            
            int cont;
            while((cont = fonemaFileIS.read(dados, 0, TAMANHO_BUFFER)) != -1) {   
            	zipFileOS.write(dados, 0, cont);   
            }
            
            entry = new ZipEntry("fonemas_pacientes.sql");   
            zipFileOS.putNextEntry(entry);  
            
            while((cont = fonemaPacFileIS.read(dados, 0, TAMANHO_BUFFER)) != -1) {   
            	zipFileOS.write(dados, 0, cont);   
            }
            
            entry = new ZipEntry("fonemas_mae_pacientes.sql");
            zipFileOS.putNextEntry(entry);
            
            while((cont = fonemaMaePacFileIS.read(dados, 0, TAMANHO_BUFFER)) != -1) {   
            	zipFileOS.write(dados, 0, cont);
            }
            
            entry = new ZipEntry("fonemas_social_pacientes.sql");
            zipFileOS.putNextEntry(entry);
            
            while((cont = fonemaSocialPacFileIS.read(dados, 0, TAMANHO_BUFFER)) != -1) {   
            	zipFileOS.write(dados, 0, cont);
            }
            
            LOG.info("Compactação concluída.");

            fonemaFile.delete();
			fonemaPacFile.delete();
			fonemaMaePacFile.delete();
			fonemaSocialPacFile.delete();
			
			this.commitTransaction();
			
			return zipFile;
		} catch (Exception e) {
			LOG.error("Ocorreu um erro na configuração da transação.", e);
			this.rollbackTransaction();
			
			return null;
		} finally {
			
			IOUtils.closeQuietly(fonemaFileOS);
			IOUtils.closeQuietly(fonemaPacFileOS);
			IOUtils.closeQuietly(fonemaMaePacFileOS);
			IOUtils.closeQuietly(fonemaSocialPacFileOS);
			IOUtils.closeQuietly(zipFileOS);
			IOUtils.closeQuietly(fonemaFileIS);
			IOUtils.closeQuietly(fonemaPacFileIS);
			IOUtils.closeQuietly(fonemaMaePacFileIS);
			IOUtils.closeQuietly(fonemaSocialPacFileIS);
			
			if (fonemaFile != null) {
				fonemaFile.delete();
			}
			if (fonemaPacFile != null) {
				fonemaPacFile.delete();
 			}
			if (fonemaMaePacFile != null) {
				fonemaMaePacFile.delete();
			}			
			if (fonemaSocialPacFile != null) {
				fonemaSocialPacFile.delete();
			}
		}
	}
	
	private Integer fonetizaNomeMaePaciente(AipPacientesRotinaFonema paciente, Map<String, Fonema> fonemas, Integer seqFonemaMae, 
			OutputStream fileOS) throws IOException, ApplicationBusinessException {
		List<String> fonemasMaePaciente = this.obterFonemasNaOrdem(paciente.getNomeMae());
		Set<String> fonemasMaePacienteSet = new HashSet<String>(fonemasMaePaciente);
		
		for (String fonemaMaePaciente : fonemasMaePacienteSet) {
			Fonema fonema = recuperaFonema(fonemas, fonemaMaePaciente);
			fonema.adicionaContadorMae();
			
			fileOS.write("INSERT INTO AGH.AIP_FONEMAS_MAE_PACIENTE(PAC_CODIGO,FON_FONEMA,SEQ) VALUES(".getBytes());
			fileOS.write(String.valueOf(paciente.getCodigo()).getBytes());
			fileOS.write(",'".getBytes());
			fileOS.write(String.valueOf(fonemaMaePaciente).getBytes());
			fileOS.write("',".getBytes());
			fileOS.write(String.valueOf(seqFonemaMae).getBytes());
			fileOS.write(END_NL.getBytes());
			
			for (int i = 0; i < fonemasMaePaciente.size(); i++) {
				String _fonemaMaePaciente = fonemasMaePaciente.get(i);
				if (fonemaMaePaciente.equals(_fonemaMaePaciente)) {
					fileOS.write("INSERT INTO AGH.AIP_POSI_FONEMAS_MAE_PACIENTES(FMP_SEQ,POSICAO) VALUES(".getBytes());
					fileOS.write(String.valueOf(seqFonemaMae).getBytes());
					fileOS.write(",".getBytes());
					fileOS.write(String.valueOf(i + 1).getBytes());
					fileOS.write(END_NL.getBytes());
				}
			}
			seqFonemaMae++;
		}
		return seqFonemaMae;
	}
	
	private Integer fonetizaNomeSocialPaciente(AipPacientesRotinaFonema paciente, Map<String, Fonema> fonemas, 
			Integer seqFonemaNomeSocial, OutputStream fileOS) throws IOException, ApplicationBusinessException {
		List<String> fonemasNomeSocialPaciente = this.obterFonemasNaOrdem(paciente.getNomeSocial());
		Set<String> fonemasNomeSocialPacienteSet = new HashSet<String>(fonemasNomeSocialPaciente);
		
		for (String fonemaNomeSocialPaciente : fonemasNomeSocialPacienteSet) {
			Fonema fonema = recuperaFonema(fonemas, fonemaNomeSocialPaciente);
			fonema.adicionaContadorNomeSocial();
			
			fileOS.write("INSERT INTO AGH.AIP_FONEMA_NOME_SOCIAL_PACIENTES(PAC_CODIGO, FON_FONEMA, SEQ) VALUES(".getBytes());
			fileOS.write(String.valueOf(paciente.getCodigo()).getBytes());
			fileOS.write(",'".getBytes());
			fileOS.write(String.valueOf(fonemaNomeSocialPaciente).getBytes());
			fileOS.write("',".getBytes());
			fileOS.write(String.valueOf(seqFonemaNomeSocial).getBytes());
			fileOS.write(END_NL.getBytes());
			
			for (int i = 0; i < fonemasNomeSocialPaciente.size(); i++) {
				String _fonemaNomeSocialPaciente = fonemasNomeSocialPaciente.get(i);
				if (fonemaNomeSocialPaciente.equals(_fonemaNomeSocialPaciente)) {
					fileOS.write("INSERT INTO AGH.AIP_POSI_FONEMAS_NOME_SOCIAL_PACIENTES(FNP_SEQ, POSICAO) VALUES(".getBytes());
					fileOS.write(String.valueOf(seqFonemaNomeSocial).getBytes());
					fileOS.write(",".getBytes());
					fileOS.write(String.valueOf(i + 1).getBytes());
					fileOS.write(END_NL.getBytes());
				}
			}
			seqFonemaNomeSocial++;
		}
		return seqFonemaNomeSocial;
	}

	private Fonema recuperaFonema(Map<String, Fonema> fonemas, String fonemaMaePaciente) {
		Fonema fonema = null;
		if (fonemas.containsKey(fonemaMaePaciente)) {
			fonema = fonemas.get(fonemaMaePaciente);
		} else {
			fonema = new Fonema(fonemaMaePaciente);
			fonemas.put(fonemaMaePaciente, fonema);
		}
		return fonema;
	}

	private Integer fonetizaNomePacientes(AipPacientesRotinaFonema paciente, Map<String, Fonema> fonemas, Integer seqNomeFonema, 
			OutputStream fileOS) throws ApplicationBusinessException, IOException {
		List<String> fonemasPaciente = this.obterFonemasNaOrdem(paciente.getNome());
		Set<String> fonemasPacienteSet = new HashSet<String>(fonemasPaciente);
		
		for (String fonemaPaciente : fonemasPacienteSet) {
			Fonema fonema = recuperaFonema(fonemas, fonemaPaciente);
			fonema.adicionaContador();
			
			fileOS.write("INSERT INTO AGH.AIP_FONEMA_PACIENTES(PAC_CODIGO,FON_FONEMA,SEQ) VALUES(".getBytes());
			fileOS.write(String.valueOf(paciente.getCodigo()).getBytes());
			fileOS.write(",'".getBytes());
			fileOS.write(String.valueOf(fonemaPaciente).getBytes());
			fileOS.write("',".getBytes());
			fileOS.write(String.valueOf(seqNomeFonema).getBytes());
			fileOS.write(END_NL.getBytes());
			
			for (int i = 0; i < fonemasPaciente.size(); i++) {
				String _fonemaPaciente = fonemasPaciente.get(i);
				if (fonemaPaciente.equals(_fonemaPaciente)) {
					fileOS.write("INSERT INTO AGH.AIP_POSI_FONEMAS_PACIENTES(FNP_SEQ,POSICAO) VALUES(".getBytes());
					fileOS.write(String.valueOf(seqNomeFonema).getBytes());
					fileOS.write(",".getBytes());
					fileOS.write(String.valueOf(i + 1).getBytes());
					fileOS.write(END_NL.getBytes());
				}
			}
			seqNomeFonema++;

		}
		return seqNomeFonema;
	}
	
	
	
	static final int TAMANHO_BUFFER = 2048; // 2kb
	static int contFiles = 1;

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	private File criaArquivoTemporario() throws Exception {
		File file = null;
		// Tentarei criar o arquivo temporário por 10 vezes
		int aux = 0;
		do {
			file = new File(contFiles++ + " - " + new Date().getTime() + ".tmp");
		} while(aux++ < 10 && !file.createNewFile());
		
		if (!file.exists()) {
			throw new Exception("Erro ao criar o arquivo temporário.");
		}
		
		return file;
	}

	private class Fonema {

		private String fonema;

		private Integer contador;
		
		private Integer contadorMae;
		private Integer contadorNomeSocial;
		
		public Fonema(String fonema) {
			this.fonema = fonema;
			this.contador = 0;
			this.contadorMae = 0;
			this.contadorNomeSocial= 0;
		}
		
		public String getFonema() {
			return fonema;
		}

		public Integer getContador() {
			return contador;
		}

		public Integer getContadorMae() {
			return contadorMae;
		}

		public void adicionaContador() {
			this.contador++;
		}

		public void adicionaContadorMae() {
			this.contadorMae++;
		}
		
		public void adicionaContadorNomeSocial() {
			this.contadorNomeSocial++;
		}
		
		public Integer getContadorNomeSocial() {
			return contadorNomeSocial;
		}

		public void setContadorNomeSocial(Integer contadorNomeSocial) {
			this.contadorNomeSocial = contadorNomeSocial;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((fonema == null) ? 0 : fonema.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Fonema other = (Fonema) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (fonema == null) {
				if (other.fonema != null) {
					return false;
				}
			} else if (!fonema.equals(other.fonema)) {
				return false;
			}
			return true;
		}

		private FonemasPacienteReindexacoesRN getOuterType() {
			return FonemasPacienteReindexacoesRN.this;
		}
		
	}
	
	
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected AipFonemasMaePacienteDAO getAipFonemasMaePacienteDAO() {
		return aipFonemasMaePacienteDAO;
	}
	
	protected AipPacientesRotinaFonemaDAO getAipPacientesRotinaFonemaDAO() {
		return aipPacientesRotinaFonemaDAO;
	}
	
	protected AipPosicaoFonemasPacientesRotinaFonemaDAO getAipPosicaoFonemasPacientesRotinaFonemaDAO() {
		return aipPosicaoFonemasPacientesRotinaFonemaDAO;
	}
	
	protected AipPosicaoFonemasMaePacienteRotinaFonemaDAO getAipPosicaoFonemasMaePacienteRotinaFonemaDAO() {
		return aipPosicaoFonemasMaePacienteRotinaFonemaDAO;
	}
}